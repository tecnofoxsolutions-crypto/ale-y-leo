package com.aleyleo.api

import com.aleyleo.api.artesania.Artesania
import com.aleyleo.api.artesania.ArtesaniaRepository
import com.aleyleo.api.categoria.Categoria
import com.aleyleo.api.categoria.CategoriaRepository
import com.aleyleo.api.config.JwtService
import com.aleyleo.api.pedido.PedidoRepository
import com.aleyleo.api.usuario.Rol
import com.aleyleo.api.usuario.Usuario
import com.aleyleo.api.usuario.UsuarioRepository
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.matchesPattern
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

// BE-13 a BE-16: crear, pagar, cancelar, cambiar estado y consultar pedidos (endpoints 9 a 13 y 23 a 26).
// Contra el MySQL real; @Transactional deshace todo al terminar cada prueba.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PedidoApiTest {

    @Autowired lateinit var mvc: MockMvc
    @Autowired lateinit var json: ObjectMapper
    @Autowired lateinit var jwtService: JwtService
    @Autowired lateinit var usuarios: UsuarioRepository
    @Autowired lateinit var categorias: CategoriaRepository
    @Autowired lateinit var artesanias: ArtesaniaRepository
    @Autowired lateinit var pedidos: PedidoRepository

    private lateinit var sofia: Usuario
    private lateinit var matias: Usuario
    private lateinit var paila: Artesania     // stock 5, $14.000
    private lateinit var colgante: Artesania  // stock 1, $28.500
    private lateinit var tokenSofia: String
    private lateinit var tokenMatias: String
    private val tokenAdmin by lazy { "Bearer " + jwtService.generarToken(1, "admin@aleyleo.cl", Rol.ADMIN) }

    @BeforeEach
    fun datos() {
        sofia = usuarios.save(cliente("zz.sofia@correo.cl", "77.000.001-1"))
        matias = usuarios.save(cliente("zz.matias@correo.cl", "77.000.002-2"))
        tokenSofia = "Bearer " + jwtService.generarToken(sofia.id!!, sofia.email, Rol.CLIENTE)
        tokenMatias = "Bearer " + jwtService.generarToken(matias.id!!, matias.email, Rol.CLIENTE)

        val categoria = categorias.save(Categoria(nombre = "ZZ Pedidos"))
        paila = artesanias.save(pieza(categoria, "ZZ Paila de Cobre", precio = 14000, stock = 5))
        colgante = artesanias.save(pieza(categoria, "ZZ Colgante", precio = 28500, stock = 1))
    }

    private fun cliente(email: String, rut: String) = Usuario(
        rut = rut, nombreCompleto = "Cliente $email", email = email, telefono = "+56911111111",
        direccion = "Av. Siempre Viva 123", comuna = "Providencia", passwordHash = "x"
    )

    private fun pieza(categoria: Categoria, nombre: String, precio: Int, stock: Int) = Artesania(
        categoria = categoria, nombre = nombre, descripcion = "Pieza de prueba",
        materialesTecnica = "Prueba", precioBase = precio, stock = stock
    )

    private fun crearPedido(token: String, vararg items: Pair<Artesania, Int>, notas: String = "") =
        mvc.post("/api/pedidos") {
            header("Authorization", token)
            contentType = MediaType.APPLICATION_JSON
            content = """
                { "items": [${items.joinToString { """{ "artesaniaId": ${it.first.id}, "cantidad": ${it.second} }""" }}],
                  "direccionDespacho": "Av. Siempre Viva 123", "comunaDespacho": "Providencia", "notas": "$notas" }
            """.trimIndent()
        }

    private fun cuerpo(acciones: ResultActionsDsl): JsonNode = json.readTree(acciones.andReturn().response.contentAsString)

    private fun idDe(acciones: ResultActionsDsl): Long = cuerpo(acciones)["id"].asLong()

    private fun stockDe(artesania: Artesania) = artesanias.findById(artesania.id!!).get().stock

    private fun cambiarEstado(id: Long, estado: String) = mvc.patch("/api/admin/pedidos/$id/estado") {
        header("Authorization", tokenAdmin)
        contentType = MediaType.APPLICATION_JSON
        content = """{ "estado": "$estado" }"""
    }

    // ---------- BE-13: crear (HU-10) ----------

    @Test
    fun crearPedidoQuedaGeneradaConCodigoTotalYSinDescontarStock() {
        crearPedido(tokenSofia, paila to 2, colgante to 1, notas = "Envolver para regalo").andExpect {
            status { isCreated() }
            jsonPath("$.estado") { value("GENERADA") }
            jsonPath("$.codigo") { value(matchesPattern("PED-\\d{4}-\\d{4,}")) }
            jsonPath("$.total") { value(2 * 14000 + 28500) }
            jsonPath("$.notas") { value("Envolver para regalo") }
            jsonPath("$.detalles.length()") { value(2) }
            jsonPath("$.detalles[0].precioUnitario") { value(14000) }
            jsonPath("$.detalles[0].subtotal") { value(28000) }
        }
        assertEquals(5, stockDe(paila)) // el stock se descuenta al pagar, no al crear
    }

    @Test
    fun sinStockNoSeCreaElPedido() {
        val antes = pedidos.count()
        crearPedido(tokenSofia, colgante to 2).andExpect {
            status { isConflict() }
            jsonPath("$.error") { value("STOCK_INSUFICIENTE") }
            jsonPath("$.mensaje") { value("No hay stock suficiente de 'ZZ Colgante'") }
        }
        assertEquals(antes, pedidos.count())
    }

    @Test
    fun validacionesDelPedido() {
        mvc.post("/api/pedidos") {
            header("Authorization", tokenSofia)
            contentType = MediaType.APPLICATION_JSON
            content = """{ "items": [], "direccionDespacho": "", "comunaDespacho": "" }"""
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.campos.items") { value("El carro está vacío") }
            jsonPath("$.campos.direccionDespacho") { exists() }
            jsonPath("$.campos.comunaDespacho") { exists() }
        }
        crearPedido(tokenSofia, paila to 0).andExpect {
            status { isBadRequest() }
            jsonPath("$.campos['items[0].cantidad']") { value("La cantidad mínima es 1") }
        }
    }

    // ---------- BE-14: pagar (HU-11) ----------

    @Test
    fun pagarPasaAPagadaYDescuentaStock() {
        val id = idDe(crearPedido(tokenSofia, paila to 2))

        mvc.post("/api/pedidos/$id/pagar") { header("Authorization", tokenSofia) }.andExpect {
            status { isOk() }
            jsonPath("$.estado") { value("PAGADA") }
            jsonPath("$.fechaPago") { exists() }
        }
        assertEquals(3, stockDe(paila))
    }

    @Test
    fun pagarSinStockResponde409YNoCambiaNada() {
        // Los dos piden el único colgante; Matías paga primero.
        val pedidoSofia = idDe(crearPedido(tokenSofia, paila to 2, colgante to 1))
        val pedidoMatias = idDe(crearPedido(tokenMatias, colgante to 1))
        mvc.post("/api/pedidos/$pedidoMatias/pagar") { header("Authorization", tokenMatias) }
            .andExpect { status { isOk() } }

        mvc.post("/api/pedidos/$pedidoSofia/pagar") { header("Authorization", tokenSofia) }.andExpect {
            status { isConflict() }
            jsonPath("$.error") { value("STOCK_INSUFICIENTE") }
        }
        // Sin cambios: sigue GENERADA y la paila no se descontó.
        mvc.get("/api/pedidos/$pedidoSofia") { header("Authorization", tokenSofia) }
            .andExpect { jsonPath("$.estado") { value("GENERADA") } }
        assertEquals(5, stockDe(paila))
    }

    // ---------- BE-15: cancelar y cambio de estado (HU-13 y HU-15) ----------

    @Test
    fun cancelarSoloDesdeGenerada() {
        val id = idDe(crearPedido(tokenSofia, paila to 1))

        mvc.post("/api/pedidos/$id/cancelar") { header("Authorization", tokenSofia) }
            .andExpect { jsonPath("$.estado") { value("CANCELADA") } }

        // Ya cancelado: no se puede cancelar ni pagar (409).
        listOf("cancelar", "pagar").forEach { accion ->
            mvc.post("/api/pedidos/$id/$accion") { header("Authorization", tokenSofia) }.andExpect {
                status { isConflict() }
                jsonPath("$.error") { value("TRANSICION_INVALIDA") }
            }
        }
        assertEquals(5, stockDe(paila))
    }

    @Test
    fun adminRecorreLasTransicionesValidas() {
        val id = idDe(crearPedido(tokenSofia, paila to 1))
        mvc.post("/api/pedidos/$id/pagar") { header("Authorization", tokenSofia) }

        // PAGADA no puede saltar a FINALIZADA.
        cambiarEstado(id, "FINALIZADA").andExpect {
            status { isConflict() }
            jsonPath("$.error") { value("TRANSICION_INVALIDA") }
            jsonPath("$.mensaje") { value("No se puede pasar de PAGADA a FINALIZADA") }
        }
        cambiarEstado(id, "RECIBIDA").andExpect { jsonPath("$.estado") { value("RECIBIDA") } }
        cambiarEstado(id, "EN_PREPARACION").andExpect { jsonPath("$.estado") { value("EN_PREPARACION") } }
        cambiarEstado(id, "FINALIZADA").andExpect { jsonPath("$.estado") { value("FINALIZADA") } }
        // FINALIZADA es final.
        cambiarEstado(id, "RECHAZADA").andExpect { status { isConflict() } }
    }

    @Test
    fun adminNoPuedeMoverUnPedidoGenerada() {
        val id = idDe(crearPedido(tokenSofia, paila to 1))
        cambiarEstado(id, "PAGADA").andExpect { status { isConflict() } }
    }

    @Test
    fun rechazarReponeElStock() {
        val id = idDe(crearPedido(tokenSofia, paila to 2))
        mvc.post("/api/pedidos/$id/pagar") { header("Authorization", tokenSofia) }
        assertEquals(3, stockDe(paila))

        cambiarEstado(id, "RECHAZADA").andExpect { jsonPath("$.estado") { value("RECHAZADA") } }
        assertEquals(5, stockDe(paila))
    }

    // ---------- BE-16: consultas (HU-12 y HU-14) ----------

    @Test
    fun misPedidosSoloLosPropiosYDelMasNuevo() {
        val primero = idDe(crearPedido(tokenSofia, paila to 1))
        val segundo = idDe(crearPedido(tokenSofia, paila to 1))
        idDe(crearPedido(tokenMatias, paila to 1))

        val ids = cuerpo(mvc.get("/api/pedidos/mis-pedidos") { header("Authorization", tokenSofia) }).map { it["id"].asLong() }
        assertEquals(listOf(segundo, primero), ids)
    }

    @Test
    fun pedidoAjenoResponde403() {
        val id = idDe(crearPedido(tokenSofia, paila to 1))

        mvc.get("/api/pedidos/$id") { header("Authorization", tokenMatias) }.andExpect {
            status { isForbidden() }
            jsonPath("$.error") { value("ACCESO_DENEGADO") }
        }
        mvc.post("/api/pedidos/$id/pagar") { header("Authorization", tokenMatias) }
            .andExpect { status { isForbidden() } }
        mvc.get("/api/pedidos/999999") { header("Authorization", tokenSofia) }
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun adminListaFiltraYVeElCliente() {
        val generada = idDe(crearPedido(tokenSofia, paila to 1))
        val pagada = idDe(crearPedido(tokenMatias, paila to 1))
        mvc.post("/api/pedidos/$pagada/pagar") { header("Authorization", tokenMatias) }

        val pagadas = cuerpo(mvc.get("/api/admin/pedidos?estado=PAGADA") { header("Authorization", tokenAdmin) })
        assertTrue(pagadas.all { it["estado"].asText() == "PAGADA" })
        assertTrue(pagadas.any { it["id"].asLong() == pagada })
        assertTrue(pagadas.none { it["id"].asLong() == generada })

        mvc.get("/api/admin/pedidos/$generada") { header("Authorization", tokenAdmin) }.andExpect {
            status { isOk() }
            jsonPath("$.cliente.email") { value("zz.sofia@correo.cl") }
            jsonPath("$.cliente.rut") { value("77.000.001-1") }
            jsonPath("$.direccionDespacho") { value("Av. Siempre Viva 123") }
        }
        // Al cliente no se le muestra el bloque "cliente".
        mvc.get("/api/pedidos/$generada") { header("Authorization", tokenSofia) }
            .andExpect { jsonPath("$.cliente") { doesNotExist() } }
    }

    @Test
    fun resumenCuentaPorEstado() {
        val antes = cuerpo(mvc.get("/api/admin/resumen") { header("Authorization", tokenAdmin) })
        idDe(crearPedido(tokenSofia, paila to 1))

        val despues = cuerpo(mvc.get("/api/admin/resumen") { header("Authorization", tokenAdmin) })
        assertEquals(antes["porEstado"]["GENERADA"].asLong() + 1, despues["porEstado"]["GENERADA"].asLong())
        assertEquals(antes["total"].asLong() + 1, despues["total"].asLong())
        assertEquals(7, despues["porEstado"].size()) // los 7 estados, aunque tengan 0
    }

    @Test
    fun cadaRolEnSusRutas() {
        // La admin no compra; el cliente no administra.
        crearPedido(tokenAdmin, paila to 1).andExpect { status { isForbidden() } }
        mvc.get("/api/admin/pedidos") { header("Authorization", tokenSofia) }.andExpect { status { isForbidden() } }
        mvc.get("/api/admin/resumen") { header("Authorization", tokenSofia) }.andExpect { status { isForbidden() } }
        mvc.get("/api/pedidos/mis-pedidos").andExpect { status { isUnauthorized() } }
    }
}
