package com.aleyleo.api

import com.aleyleo.api.artesania.Artesania
import com.aleyleo.api.artesania.ArtesaniaRepository
import com.aleyleo.api.categoria.Categoria
import com.aleyleo.api.categoria.CategoriaRepository
import com.aleyleo.api.pedido.EstadoPedido
import com.aleyleo.api.pedido.Pedido
import com.aleyleo.api.pedido.PedidoRepository
import com.aleyleo.api.usuario.Usuario
import com.aleyleo.api.usuario.UsuarioRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDateTime

// BE-04: las consultas de los repositorios, contra el MySQL real.
// @DataJpaTest deshace (rollback) todo lo que cada prueba guarda, así la BD queda igual.
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RepositoriosTest {

    @Autowired lateinit var categorias: CategoriaRepository
    @Autowired lateinit var artesanias: ArtesaniaRepository
    @Autowired lateinit var usuarios: UsuarioRepository
    @Autowired lateinit var pedidos: PedidoRepository

    private fun categoria(nombre: String, activo: Boolean = true) =
        categorias.save(Categoria(nombre = nombre, activo = activo))

    private fun artesania(categoria: Categoria, nombre: String, materiales: String = "Greda", activo: Boolean = true) =
        artesanias.save(
            Artesania(
                categoria = categoria, nombre = nombre, descripcion = "Pieza de prueba",
                materialesTecnica = materiales, precioBase = 1000, stock = 1, activo = activo
            )
        )

    private fun usuario(email: String, rut: String) = usuarios.save(
        Usuario(
            rut = rut, nombreCompleto = "Prueba", email = email, telefono = "+56911111111",
            direccion = "Calle 1", comuna = "Santiago", passwordHash = "x"
        )
    )

    @Test
    fun buscarActivasFiltraPorCategoriaYTexto() {
        val greda = categoria("ZZ Greda prueba")
        val cobre = categoria("ZZ Cobre prueba")
        val oculta = categoria("ZZ Oculta prueba", activo = false)
        artesania(greda, "ZZ Jarro azul")
        artesania(greda, "ZZ Plato", materiales = "Greda azul esmaltada")
        artesania(greda, "ZZ Inactiva azul", activo = false)
        artesania(cobre, "ZZ Paila azul")
        artesania(oculta, "ZZ De categoría oculta azul")

        // Texto en nombre o materiales, sin mayúsculas; sin inactivas ni de categorías inactivas.
        val conTexto = artesanias.buscarActivas(null, "AZUL").map { it.nombre }.filter { it.startsWith("ZZ") }
        assertEquals(listOf("ZZ Jarro azul", "ZZ Paila azul", "ZZ Plato"), conTexto)

        // Categoría + texto.
        assertEquals(listOf("ZZ Jarro azul", "ZZ Plato"), artesanias.buscarActivas(greda.id, "azul").map { it.nombre })

        // La admin ve también la inactiva.
        assertTrue(artesanias.findAllConCategoria().any { it.nombre == "ZZ Inactiva azul" })
        assertEquals(2, artesanias.countByCategoriaIdAndActivoTrue(greda.id!!))
    }

    @Test
    fun categoriasActivasYNombreUnico() {
        val visible = categoria("ZZ Visible")
        categoria("ZZ No visible", activo = false)

        val activas = categorias.findByActivoTrueOrderByNombreAsc().map { it.nombre }
        assertTrue("ZZ Visible" in activas)
        assertFalse("ZZ No visible" in activas)

        assertTrue(categorias.existsByNombreIgnoreCase("zz visible"))
        assertFalse(categorias.existsByNombreIgnoreCaseAndIdNot("ZZ Visible", visible.id!!))
    }

    @Test
    fun usuarioPorEmailYRut() {
        usuario("zz.prueba@correo.cl", "11.111.111-1")

        assertNotNull(usuarios.findByEmailIgnoreCase("ZZ.Prueba@Correo.cl"))
        assertTrue(usuarios.existsByEmailIgnoreCase("zz.prueba@correo.cl"))
        assertTrue(usuarios.existsByRut("11.111.111-1"))
    }

    @Test
    fun pedidosDelUsuarioYConteoPorEstado() {
        val cliente = usuario("zz.pedidos@correo.cl", "22.222.222-2")
        val antes = pedidos.contarPorEstado().associate { (it[0] as EstadoPedido) to (it[1] as Long) }

        pedidos.save(Pedido(codigo = "ZZ-1", usuario = cliente, fechaCreacion = LocalDateTime.now().minusDays(1)))
        pedidos.save(Pedido(codigo = "ZZ-2", usuario = cliente, estado = EstadoPedido.PAGADA))

        // Del más nuevo al más antiguo.
        assertEquals(listOf("ZZ-2", "ZZ-1"), pedidos.findByUsuarioIdOrderByFechaCreacionDesc(cliente.id!!).map { it.codigo })

        val despues = pedidos.contarPorEstado().associate { (it[0] as EstadoPedido) to (it[1] as Long) }
        assertEquals((antes[EstadoPedido.PAGADA] ?: 0) + 1, despues[EstadoPedido.PAGADA])
    }
}
