package com.aleyleo.api

import com.aleyleo.api.artesania.ArtesaniaRepository
import com.aleyleo.api.categoria.CategoriaRepository
import com.aleyleo.api.config.DataSeeder
import com.aleyleo.api.pedido.EstadoPedido
import com.aleyleo.api.pedido.PedidoRepository
import com.aleyleo.api.usuario.Rol
import com.aleyleo.api.usuario.UsuarioRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

// BE-05: con la BD vacía, el DataSeeder deja 4 categorías, 20 artesanías, 4 usuarios y 7 pedidos (sección 5.4).
// Los datos de otras pruebas empiezan con "ZZ" y se deshacen, por eso aquí se filtran.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DatosPruebaTest {

    @Autowired lateinit var seeder: DataSeeder
    @Autowired lateinit var mvc: MockMvc
    @Autowired lateinit var categorias: CategoriaRepository
    @Autowired lateinit var artesanias: ArtesaniaRepository
    @Autowired lateinit var usuarios: UsuarioRepository
    @Autowired lateinit var pedidos: PedidoRepository

    private val emailsDePrueba = setOf("admin@aleyleo.cl", "sofia@correo.cl", "matias@correo.cl", "camila@correo.cl")

    private fun categoriasSemilla() = categorias.findAll().filter { !it.nombre.startsWith("ZZ") }
    private fun artesaniasSemilla() = artesanias.findAll().filter { !it.nombre.startsWith("ZZ") }
    private fun pedidosSemilla() = pedidos.findAll().filter { it.codigo.matches(Regex("PED-2026-000[1-7]")) }

    @Test
    fun hay4Categorias20Artesanias4UsuariosY7Pedidos() {
        assertEquals(setOf("Orfebrería", "Cobre", "Alfarería", "Textil"), categoriasSemilla().map { it.nombre }.toSet())
        assertEquals(20, artesaniasSemilla().size)
        assertTrue(usuarios.findAll().map { it.email }.containsAll(emailsDePrueba))
        assertEquals(7, pedidosSemilla().size)
    }

    @Test
    fun cincoArtesaniasPorCategoriaYAlMenosDosAgotadas() {
        artesaniasSemilla().groupBy { it.categoria.nombre }.values.forEach { assertEquals(5, it.size) }
        assertTrue(artesaniasSemilla().count { it.stock == 0 } >= 2)
        assertTrue(artesaniasSemilla().all { it.stock in 0..5 })
    }

    @Test
    fun unPedidoEnCadaEstadoConTotalCorrecto() {
        val porEstado = pedidosSemilla().groupBy { it.estado }
        EstadoPedido.entries.forEach { assertEquals(1, porEstado[it]?.size, "Estado $it") }

        val pedido7 = pedidosSemilla().first { it.codigo == "PED-2026-0007" }
        assertEquals(EstadoPedido.GENERADA, pedido7.estado)
        assertEquals(28500 + 2 * 13500, pedido7.total)
        assertEquals("sofia@correo.cl", pedido7.usuario.email)
    }

    @Test
    fun soloUnaAdminYContrasenasCifradas() {
        val deSemilla = usuarios.findAll().filter { it.email in emailsDePrueba }
        assertEquals(listOf("admin@aleyleo.cl"), deSemilla.filter { it.rol == Rol.ADMIN }.map { it.email })
        assertTrue(deSemilla.all { it.passwordHash.startsWith("\$2a\$") })
    }

    @Test
    fun lasCuentasDePruebaPuedenIniciarSesion() {
        mvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{ "email": "admin@aleyleo.cl", "password": "Admin123" }"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.usuario.rol") { value("ADMIN") }
        }
        mvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{ "email": "sofia@correo.cl", "password": "Cliente123" }"""
        }.andExpect { status { isOk() } }
    }

    @Test
    fun ejecutarloOtraVezNoDuplica() {
        val antes = artesanias.count()
        seeder.run()
        assertEquals(antes, artesanias.count())
        assertEquals(7, pedidosSemilla().size)
    }
}
