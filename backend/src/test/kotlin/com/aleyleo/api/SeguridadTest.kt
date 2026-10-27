package com.aleyleo.api

import com.aleyleo.api.config.JwtService
import com.aleyleo.api.config.UsuarioAutenticado
import com.aleyleo.api.usuario.Rol
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

// BE-07 y BE-08: rutas públicas, protegidas y de admin, con tokens válidos, alterados y vencidos.
@SpringBootTest
@AutoConfigureMockMvc
@Import(SeguridadTest.ControladorSeguridadPrueba::class)
class SeguridadTest {

    // Rutas de prueba con los mismos prefijos que la API real.
    @RestController
    class ControladorSeguridadPrueba {
        @GetMapping("/api/artesanias/prueba-publica")
        fun publica() = "catálogo público"

        @GetMapping("/api/usuarios/prueba-me")
        fun me(@AuthenticationPrincipal usuario: UsuarioAutenticado) = usuario

        @GetMapping("/api/admin/prueba")
        fun admin() = "solo admin"
    }

    @Autowired lateinit var mvc: MockMvc
    @Autowired lateinit var jwtService: JwtService
    @Autowired lateinit var passwordEncoder: PasswordEncoder

    private val tokenCliente by lazy { jwtService.generarToken(2, "sofia@correo.cl", Rol.CLIENTE) }
    private val tokenAdmin by lazy { jwtService.generarToken(1, "admin@aleyleo.cl", Rol.ADMIN) }

    @Test
    fun rutaPublicaSinToken() {
        mvc.get("/api/artesanias/prueba-publica").andExpect { status { isOk() } }
    }

    @Test
    fun rutaProtegidaSinTokenResponde401() {
        mvc.get("/api/usuarios/prueba-me").andExpect {
            status { isUnauthorized() }
            jsonPath("$.status") { value(401) }
            jsonPath("$.error") { value("NO_AUTENTICADO") }
        }
    }

    @Test
    fun conTokenValidoSeAccedeYSeSabeQuienEs() {
        mvc.get("/api/usuarios/prueba-me") {
            header("Authorization", "Bearer $tokenCliente")
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(2) }
            jsonPath("$.email") { value("sofia@correo.cl") }
            jsonPath("$.rol") { value("CLIENTE") }
        }
    }

    @Test
    fun conTokenAlteradoResponde401() {
        // Se cambia un carácter de la firma (la última parte del token).
        val ultimo = tokenCliente.last()
        val alterado = tokenCliente.dropLast(1) + (if (ultimo == 'A') 'B' else 'A')

        mvc.get("/api/usuarios/prueba-me") {
            header("Authorization", "Bearer $alterado")
        }.andExpect { status { isUnauthorized() } }
    }

    @Test
    fun conTokenVencidoResponde401() {
        val vencido = jwtService.generarToken(2, "sofia@correo.cl", Rol.CLIENTE, segundos = -60)

        assertNull(jwtService.leerToken(vencido))
        mvc.get("/api/usuarios/prueba-me") {
            header("Authorization", "Bearer $vencido")
        }.andExpect { status { isUnauthorized() } }
    }

    @Test
    fun rutaAdminSoloConRolAdmin() {
        mvc.get("/api/admin/prueba") {
            header("Authorization", "Bearer $tokenCliente")
        }.andExpect {
            status { isForbidden() }
            jsonPath("$.error") { value("ACCESO_DENEGADO") }
        }
        mvc.get("/api/admin/prueba") {
            header("Authorization", "Bearer $tokenAdmin")
        }.andExpect { status { isOk() } }
    }

    @Test
    fun rutaInexistenteResponde404() {
        mvc.get("/api/no-existe") {
            header("Authorization", "Bearer $tokenCliente")
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.error") { value("NO_ENCONTRADO") }
        }
    }

    @Test
    fun tokenDuraVeinticuatroHoras() {
        assertEquals(86400, jwtService.expiracionSegundos)
    }

    @Test
    fun contrasenaConBCrypt() {
        val hash = passwordEncoder.encode("Cliente123")

        assertNotEquals("Cliente123", hash)
        assertTrue(hash.startsWith("\$2a\$"))
        assertTrue(passwordEncoder.matches("Cliente123", hash))
    }
}
