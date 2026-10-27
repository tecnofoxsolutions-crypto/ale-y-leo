package com.aleyleo.api

import com.aleyleo.api.common.calcularDigitoVerificador
import com.aleyleo.api.common.formatearRut
import com.aleyleo.api.common.rutValido
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.transaction.annotation.Transactional

// BE-09: HU-01 (registro), HU-02 (login) y HU-03 (perfil), contra el MySQL real.
// @Transactional deshace todo al terminar cada prueba: la BD queda igual que antes.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthTest {

    @Autowired lateinit var mvc: MockMvc
    @Autowired lateinit var json: ObjectMapper

    // RUT válido a partir de un cuerpo cualquiera.
    private fun rut(cuerpo: String) = "$cuerpo-${calcularDigitoVerificador(cuerpo)}"

    private fun registroJson(
        email: String = "zz.nueva@correo.cl",
        rut: String = rut("77111222"),
        password: String = "Secreta123",
        telefono: String = "+56 9 8765 4321"
    ) = """
        { "rut": "$rut", "nombreCompleto": "Cliente de Prueba", "email": "$email",
          "telefono": "$telefono", "direccion": "Av. Siempre Viva 123", "comuna": "Providencia",
          "password": "$password" }
    """.trimIndent()

    private fun registrar(cuerpo: String = registroJson()) = mvc.post("/api/auth/registro") {
        contentType = MediaType.APPLICATION_JSON
        content = cuerpo
    }

    private fun login(email: String, password: String) = mvc.post("/api/auth/login") {
        contentType = MediaType.APPLICATION_JSON
        content = """{ "email": "$email", "password": "$password" }"""
    }

    private fun tokenDe(respuesta: String): String = json.readTree(respuesta)["token"].asText()

    // ---------- HU-01 Registro ----------

    @Test
    fun registroCreaUnClienteConSesionIniciada() {
        registrar().andExpect {
            status { isCreated() }
            jsonPath("$.token") { isNotEmpty() }
            jsonPath("$.tipo") { value("Bearer") }
            jsonPath("$.expiraEn") { value(86400) }
            jsonPath("$.usuario.email") { value("zz.nueva@correo.cl") }
            jsonPath("$.usuario.rol") { value("CLIENTE") }
            jsonPath("$.usuario.password") { doesNotExist() }
        }
    }

    @Test
    fun registroValidaTodosLosCampos() {
        registrar(
            """{ "rut": "18.432.567-4", "nombreCompleto": "", "email": "sofia@", "telefono": "22345678",
                 "direccion": "", "comuna": "", "password": "1234567" }"""
        ).andExpect {
            status { isBadRequest() }
            jsonPath("$.campos.rut") { value("El RUT no es válido") }
            jsonPath("$.campos.nombreCompleto") { exists() }
            jsonPath("$.campos.email") { value("El email no es válido") }
            jsonPath("$.campos.telefono") { exists() }
            jsonPath("$.campos.direccion") { exists() }
            jsonPath("$.campos.comuna") { exists() }
            jsonPath("$.campos.password") { value("La contraseña debe tener al menos 8 caracteres") }
        }
    }

    @Test
    fun emailORutRepetidoResponde409() {
        registrar().andExpect { status { isCreated() } }

        // Mismo email (con otras mayúsculas) y otro RUT.
        registrar(registroJson(email = "ZZ.Nueva@Correo.cl", rut = rut("77111333"))).andExpect {
            status { isConflict() }
            jsonPath("$.error") { value("EMAIL_REGISTRADO") }
            jsonPath("$.mensaje") { value("Ya existe una cuenta con ese email") }
        }
        // Mismo RUT escrito sin puntos y otro email.
        registrar(registroJson(email = "zz.otra@correo.cl", rut = rut("77111222").replace("-", ""))).andExpect {
            status { isConflict() }
            jsonPath("$.error") { value("RUT_REGISTRADO") }
        }
    }

    // ---------- HU-02 Login ----------

    @Test
    fun loginCorrectoDevuelveUnTokenQueSirve() {
        registrar().andExpect { status { isCreated() } }

        val respuesta = login("zz.nueva@correo.cl", "Secreta123").andExpect {
            status { isOk() }
            jsonPath("$.usuario.rol") { value("CLIENTE") }
        }.andReturn().response.contentAsString

        mvc.get("/api/usuarios/me") {
            header("Authorization", "Bearer ${tokenDe(respuesta)}")
        }.andExpect {
            status { isOk() }
            jsonPath("$.email") { value("zz.nueva@correo.cl") }
        }
    }

    @Test
    fun loginIncorrectoNoDiceQueFallo() {
        registrar().andExpect { status { isCreated() } }

        // Contraseña mala y email inexistente dan exactamente la misma respuesta.
        listOf(login("zz.nueva@correo.cl", "Otra12345"), login("zz.noexiste@correo.cl", "Secreta123")).forEach {
            it.andExpect {
                status { isUnauthorized() }
                jsonPath("$.error") { value("CREDENCIALES_INVALIDAS") }
                jsonPath("$.mensaje") { value("Email o contraseña incorrectos") }
            }
        }
    }

    // ---------- HU-03 Perfil ----------

    @Test
    fun perfilMuestraLosDatosYSeEditaSinCambiarEmailNiRut() {
        val token = tokenDe(registrar().andReturn().response.contentAsString)

        mvc.get("/api/usuarios/me") { header("Authorization", "Bearer $token") }.andExpect {
            status { isOk() }
            jsonPath("$.rut") { value(formatearRut(rut("77111222"))) }
            jsonPath("$.telefono") { value("+56987654321") }
            jsonPath("$.comuna") { value("Providencia") }
        }

        // Aunque se envíen email y rut, se ignoran: el DTO de edición no los tiene.
        mvc.put("/api/usuarios/me") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = """{ "nombreCompleto": "Nombre Nuevo", "telefono": "912345678", "direccion": "Calle Nueva 45",
                           "comuna": "Ñuñoa", "email": "hackeo@correo.cl", "rut": "1-9" }"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.nombreCompleto") { value("Nombre Nuevo") }
            jsonPath("$.telefono") { value("+56912345678") }
            jsonPath("$.comuna") { value("Ñuñoa") }
            jsonPath("$.email") { value("zz.nueva@correo.cl") }
            jsonPath("$.rut") { value(formatearRut(rut("77111222"))) }
        }
    }

    @Test
    fun editarPerfilValidaLosCampos() {
        val token = tokenDe(registrar().andReturn().response.contentAsString)

        mvc.put("/api/usuarios/me") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = """{ "nombreCompleto": "", "telefono": "123", "direccion": "", "comuna": "" }"""
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.campos.nombreCompleto") { exists() }
            jsonPath("$.campos.telefono") { exists() }
        }
    }

    @Test
    fun perfilSinTokenResponde401() {
        mvc.get("/api/usuarios/me").andExpect { status { isUnauthorized() } }
    }

    // ---------- Validaciones chilenas ----------

    @Test
    fun rutConDigitoVerificador() {
        assertTrue(rutValido("18.432.567-5"))
        assertTrue(rutValido("184325675"))
        assertTrue(rutValido("10.000.013-k"))
        assertFalse(rutValido("18.432.567-4"))
        assertFalse(rutValido("abc"))
        assertEquals("18.432.567-5", formatearRut("184325675"))
    }
}
