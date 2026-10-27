package com.aleyleo.api

import com.aleyleo.api.common.ConflictoException
import com.aleyleo.api.common.NoEncontradoException
import com.aleyleo.api.common.StockInsuficienteException
import com.aleyleo.api.config.JwtService
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

// BE-06: cada error responde con el formato ApiError y el código HTTP de la sección 6.2.
// Usa un controller solo de prueba; los filtros de seguridad se desactivan (los prueba SeguridadTest).
@WebMvcTest(controllers = [ManejoErroresTest.ControladorPrueba::class])
@AutoConfigureMockMvc(addFilters = false)
@Import(ManejoErroresTest.ControladorPrueba::class, JwtService::class)
class ManejoErroresTest {

    data class DtoPrueba(
        @field:NotBlank(message = "El nombre es obligatorio")
        val nombre: String?,
        @field:Email(message = "El email no es válido")
        val email: String?,
        @field:Positive(message = "El precio debe ser mayor que 0")
        val precio: Int?
    )

    @RestController
    class ControladorPrueba {
        @PostMapping("/prueba")
        fun crear(@Valid @RequestBody dto: DtoPrueba) = dto

        @GetMapping("/prueba/{id}")
        fun obtener(@PathVariable id: Long): String = when (id) {
            404L -> throw NoEncontradoException("Artesanía no encontrada")
            409L -> throw StockInsuficienteException("Paila de Cobre")
            410L -> throw ConflictoException("EMAIL_REGISTRADO", "El email ya está registrado")
            500L -> throw IllegalStateException("detalle interno que no se debe mostrar")
            else -> "ok"
        }
    }

    @Autowired
    lateinit var mvc: MockMvc

    @Test
    fun dtoInvalidoResponde400ConLaListaDeCampos() {
        mvc.post("/prueba") {
            contentType = MediaType.APPLICATION_JSON
            content = """{ "nombre": "", "email": "sofia@", "precio": 0 }"""
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.status") { value(400) }
            jsonPath("$.error") { value("VALIDACION") }
            jsonPath("$.campos.nombre") { value("El nombre es obligatorio") }
            jsonPath("$.campos.email") { value("El email no es válido") }
            jsonPath("$.campos.precio") { value("El precio debe ser mayor que 0") }
        }
    }

    @Test
    fun jsonMalFormadoResponde400() {
        mvc.post("/prueba") {
            contentType = MediaType.APPLICATION_JSON
            content = "{ esto no es json"
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.error") { value("JSON_INVALIDO") }
        }
    }

    @Test
    fun noEncontradoResponde404() {
        mvc.get("/prueba/404").andExpect {
            status { isNotFound() }
            jsonPath("$.error") { value("NO_ENCONTRADO") }
            jsonPath("$.mensaje") { value("Artesanía no encontrada") }
            jsonPath("$.campos") { doesNotExist() }
        }
    }

    @Test
    fun conflictosResponden409ConSuCodigo() {
        mvc.get("/prueba/409").andExpect {
            status { isConflict() }
            jsonPath("$.error") { value("STOCK_INSUFICIENTE") }
            jsonPath("$.mensaje") { value("No hay stock suficiente de 'Paila de Cobre'") }
        }
        mvc.get("/prueba/410").andExpect {
            status { isConflict() }
            jsonPath("$.error") { value("EMAIL_REGISTRADO") }
        }
    }

    @Test
    fun errorInesperadoResponde500SinDetallesInternos() {
        mvc.get("/prueba/500").andExpect {
            status { isInternalServerError() }
            jsonPath("$.error") { value("ERROR_INTERNO") }
            jsonPath("$.mensaje") { value("Ocurrió un error inesperado") }
        }
    }

    @Test
    fun parametroConTipoIncorrectoResponde400() {
        mvc.get("/prueba/abc").andExpect {
            status { isBadRequest() }
            jsonPath("$.error") { value("PARAMETRO_INVALIDO") }
        }
    }
}
