package com.aleyleo.api

import com.aleyleo.api.artesania.Artesania
import com.aleyleo.api.artesania.ArtesaniaRepository
import com.aleyleo.api.categoria.Categoria
import com.aleyleo.api.categoria.CategoriaRepository
import com.aleyleo.api.config.JwtService
import com.aleyleo.api.usuario.Rol
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.transaction.annotation.Transactional

// BE-10 (endpoints 6 y 14 a 17) y BE-11 (endpoints 7, 8 y 18 a 21), contra el MySQL real.
// @Transactional deshace todo al terminar cada prueba. Los datos de prueba empiezan con "ZZ".
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CatalogoApiTest {

    @Autowired lateinit var mvc: MockMvc
    @Autowired lateinit var json: ObjectMapper
    @Autowired lateinit var jwtService: JwtService
    @Autowired lateinit var categorias: CategoriaRepository
    @Autowired lateinit var artesanias: ArtesaniaRepository

    private val admin by lazy { "Bearer " + jwtService.generarToken(1, "admin@aleyleo.cl", Rol.ADMIN) }
    private val cliente by lazy { "Bearer " + jwtService.generarToken(2, "sofia@correo.cl", Rol.CLIENTE) }

    private lateinit var greda: Categoria
    private lateinit var cobre: Categoria
    private lateinit var oculta: Categoria
    private lateinit var jarro: Artesania
    private lateinit var inactiva: Artesania

    @BeforeEach
    fun datos() {
        greda = categorias.save(Categoria(nombre = "ZZ Greda", descripcion = "Greda de prueba"))
        cobre = categorias.save(Categoria(nombre = "ZZ Cobre"))
        oculta = categorias.save(Categoria(nombre = "ZZ Oculta", activo = false))
        jarro = artesanias.save(pieza(greda, "ZZ Jarro azul", materiales = "Greda pulida"))
        artesanias.save(pieza(greda, "ZZ Plato", materiales = "Greda azul esmaltada"))
        inactiva = artesanias.save(pieza(greda, "ZZ Inactiva azul", activo = false))
        artesanias.save(pieza(cobre, "ZZ Paila azul", materiales = "Cobre martillado"))
        artesanias.save(pieza(oculta, "ZZ De categoría oculta"))
    }

    private fun pieza(categoria: Categoria, nombre: String, materiales: String = "Greda", activo: Boolean = true) =
        Artesania(
            categoria = categoria, nombre = nombre, descripcion = "Pieza de prueba",
            materialesTecnica = materiales, precioBase = 10000, stock = 3, activo = activo
        )

    private fun cuerpo(acciones: ResultActionsDsl): JsonNode = json.readTree(acciones.andReturn().response.contentAsString)

    private fun nombresZZ(acciones: ResultActionsDsl): List<String> =
        cuerpo(acciones).map { it["nombre"].asText() }.filter { it.startsWith("ZZ") }

    // ---------- BE-10: categorías ----------

    @Test
    fun publicoVeSoloCategoriasActivas() {
        val nombres = nombresZZ(mvc.get("/api/categorias").andExpect { status { isOk() } })
        assertEquals(listOf("ZZ Cobre", "ZZ Greda"), nombres)
    }

    @Test
    fun adminVeTodasLasCategorias() {
        val nombres = nombresZZ(mvc.get("/api/admin/categorias") { header("Authorization", admin) })
        assertEquals(listOf("ZZ Cobre", "ZZ Greda", "ZZ Oculta"), nombres)
    }

    @Test
    fun adminCreaYEditaCategoriaConNombreUnico() {
        mvc.post("/api/admin/categorias") {
            header("Authorization", admin)
            contentType = MediaType.APPLICATION_JSON
            content = """{ "nombre": "ZZ Cestería", "descripcion": "Mimbre" }"""
        }.andExpect {
            status { isCreated() }
            jsonPath("$.nombre") { value("ZZ Cestería") }
            jsonPath("$.activo") { value(true) }
        }

        // Nombre repetido, sin importar mayúsculas.
        mvc.post("/api/admin/categorias") {
            header("Authorization", admin)
            contentType = MediaType.APPLICATION_JSON
            content = """{ "nombre": "zz greda" }"""
        }.andExpect {
            status { isConflict() }
            jsonPath("$.error") { value("NOMBRE_REPETIDO") }
        }

        // Nombre obligatorio.
        mvc.post("/api/admin/categorias") {
            header("Authorization", admin)
            contentType = MediaType.APPLICATION_JSON
            content = """{ "nombre": "" }"""
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.campos.nombre") { value("El nombre es obligatorio") }
        }

        // Editar manteniendo su propio nombre sí se puede.
        mvc.put("/api/admin/categorias/${greda.id}") {
            header("Authorization", admin)
            contentType = MediaType.APPLICATION_JSON
            content = """{ "nombre": "ZZ Greda", "descripcion": "Nueva descripción" }"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.descripcion") { value("Nueva descripción") }
        }
    }

    @Test
    fun noSeDesactivaUnaCategoriaConArtesaniasActivas() {
        mvc.delete("/api/admin/categorias/${greda.id}") { header("Authorization", admin) }.andExpect {
            status { isConflict() }
            jsonPath("$.error") { value("CATEGORIA_CON_ARTESANIAS") }
            jsonPath("$.mensaje") { value(org.hamcrest.Matchers.containsString("tiene 2 artesanía(s) activa(s)")) }
        }

        // Una categoría sin artesanías activas sí se desactiva (204) y desaparece para el cliente.
        val vacia = categorias.save(Categoria(nombre = "ZZ Vacía"))
        mvc.delete("/api/admin/categorias/${vacia.id}") { header("Authorization", admin) }
            .andExpect { status { isNoContent() } }
        assertFalse("ZZ Vacía" in nombresZZ(mvc.get("/api/categorias")))
    }

    @Test
    fun clienteNoPuedeAdministrarCategorias() {
        mvc.post("/api/admin/categorias") {
            header("Authorization", cliente)
            contentType = MediaType.APPLICATION_JSON
            content = """{ "nombre": "ZZ Intruso" }"""
        }.andExpect { status { isForbidden() } }
    }

    // ---------- BE-11: artesanías ----------

    @Test
    fun catalogoPublicoConFiltros() {
        // Sin filtros: solo activas de categorías activas.
        assertEquals(
            listOf("ZZ Jarro azul", "ZZ Paila azul", "ZZ Plato"),
            nombresZZ(mvc.get("/api/artesanias").andExpect { status { isOk() } })
        )
        // Texto (en nombre o materiales, sin mayúsculas) combinado con la categoría.
        assertEquals(
            listOf("ZZ Jarro azul", "ZZ Plato"),
            nombresZZ(mvc.get("/api/artesanias?categoriaId=${greda.id}&q=AZUL"))
        )
        // Texto en los materiales.
        assertEquals(listOf("ZZ Paila azul"), nombresZZ(mvc.get("/api/artesanias?q=martillado")))
    }

    @Test
    fun detallePublicoConCategoria() {
        mvc.get("/api/artesanias/${jarro.id}").andExpect {
            status { isOk() }
            jsonPath("$.nombre") { value("ZZ Jarro azul") }
            jsonPath("$.precioBase") { value(10000) }
            jsonPath("$.categoria.nombre") { value("ZZ Greda") }
        }
        // Inactiva o inexistente: 404 para el cliente.
        mvc.get("/api/artesanias/${inactiva.id}").andExpect { status { isNotFound() } }
        mvc.get("/api/artesanias/999999").andExpect { status { isNotFound() } }
    }

    @Test
    fun adminVeTodasLasArtesaniasIncluidasInactivas() {
        val todas = cuerpo(mvc.get("/api/admin/artesanias") { header("Authorization", admin) })
        val inactivaJson = todas.first { it["nombre"].asText() == "ZZ Inactiva azul" }
        assertFalse(inactivaJson["activo"].asBoolean())
    }

    @Test
    fun adminCreaEditaYDesactivaArtesania() {
        val creada = cuerpo(
            mvc.post("/api/admin/artesanias") {
                header("Authorization", admin)
                contentType = MediaType.APPLICATION_JSON
                content = """{ "categoriaId": ${cobre.id}, "nombre": "ZZ Bandeja", "descripcion": "Bandeja grabada",
                               "materialesTecnica": "Cobre grabado", "precioBase": 27500, "stock": 0 }"""
            }.andExpect { status { isCreated() } }
        )
        val id = creada["id"].asLong()
        assertEquals("ZZ Cobre", creada["categoria"]["nombre"].asText())

        // Editar precio y stock.
        mvc.put("/api/admin/artesanias/$id") {
            header("Authorization", admin)
            contentType = MediaType.APPLICATION_JSON
            content = """{ "categoriaId": ${cobre.id}, "nombre": "ZZ Bandeja", "descripcion": "Bandeja grabada",
                           "materialesTecnica": "Cobre grabado", "precioBase": 25000, "stock": 4 }"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.precioBase") { value(25000) }
            jsonPath("$.stock") { value(4) }
        }

        // Desactivar: 204, deja de verse en el catálogo, pero no se borra.
        mvc.delete("/api/admin/artesanias/$id") { header("Authorization", admin) }
            .andExpect { status { isNoContent() } }
        assertFalse("ZZ Bandeja" in nombresZZ(mvc.get("/api/artesanias")))
        assertTrue(artesanias.findById(id).isPresent)
    }

    @Test
    fun validacionesDelProducto() {
        mvc.post("/api/admin/artesanias") {
            header("Authorization", admin)
            contentType = MediaType.APPLICATION_JSON
            content = """{ "nombre": "", "descripcion": "", "materialesTecnica": "", "precioBase": 0, "stock": -1 }"""
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.campos.categoriaId") { value("Elige una categoría") }
            jsonPath("$.campos.nombre") { exists() }
            jsonPath("$.campos.descripcion") { exists() }
            jsonPath("$.campos.materialesTecnica") { exists() }
            jsonPath("$.campos.precioBase") { value("El precio debe ser mayor que 0") }
            jsonPath("$.campos.stock") { value("El stock no puede ser negativo") }
        }
        // Categoría inexistente: 404.
        mvc.post("/api/admin/artesanias") {
            header("Authorization", admin)
            contentType = MediaType.APPLICATION_JSON
            content = """{ "categoriaId": 999999, "nombre": "X", "descripcion": "X", "materialesTecnica": "X",
                           "precioBase": 100, "stock": 1 }"""
        }.andExpect { status { isNotFound() } }
    }

    @Test
    fun sinTokenNoSeAdministraElCatalogo() {
        mvc.get("/api/admin/artesanias").andExpect { status { isUnauthorized() } }
        mvc.delete("/api/admin/artesanias/${jarro.id}") { header("Authorization", cliente) }
            .andExpect { status { isForbidden() } }
    }
}
