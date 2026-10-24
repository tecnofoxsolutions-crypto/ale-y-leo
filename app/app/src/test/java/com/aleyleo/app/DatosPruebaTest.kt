package com.aleyleo.app

import com.aleyleo.app.datos.DatosPrueba
import com.aleyleo.app.model.EstadoPedido
import com.aleyleo.app.model.Rol
import com.aleyleo.app.repository.AuthRepository
import com.aleyleo.app.repository.CatalogoRepository
import com.aleyleo.app.repository.PedidoRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// Comprueba que los datos de prueba cumplen la sección 5.4 y que los repositorios los leen bien.
class DatosPruebaTest {

    @After
    fun limpiarSesion() {
        AuthRepository.cerrarSesion()
    }

    @Test
    fun hay4Categorias20ArtesaniasY4Usuarios() {
        assertEquals(4, DatosPrueba.categorias.size)
        assertEquals(20, DatosPrueba.artesanias.size)
        assertEquals(4, DatosPrueba.usuarios.size)
        assertEquals(1, DatosPrueba.usuarios.count { it.rol == Rol.ADMIN })
    }

    @Test
    fun cadaCategoriaTiene5Artesanias() {
        DatosPrueba.categorias.forEach { categoria ->
            assertEquals(5, DatosPrueba.artesanias.count { it.categoriaId == categoria.id })
        }
    }

    @Test
    fun hayAlMenos2ArtesaniasAgotadas() {
        assertTrue(DatosPrueba.artesanias.count { it.agotada } >= 2)
    }

    @Test
    fun hayUnPedidoEnCadaEstado() {
        EstadoPedido.entries.forEach { estado ->
            assertEquals(1, DatosPrueba.pedidos.count { it.estado == estado })
        }
    }

    @Test
    fun totalDelPedidoEsLaSumaDeSubtotales() {
        val pedido = PedidoRepository.obtenerPedido(7)!!
        assertEquals(28500 + 2 * 13500, pedido.total)
    }

    @Test
    fun catalogoEncuentraArtesaniaPorId() {
        assertEquals("Paila de Cobre", CatalogoRepository.obtenerArtesania(6)?.nombre)
        assertNull(CatalogoRepository.obtenerArtesania(999))
    }

    @Test
    fun loginCorrectoGuardaLaSesion() {
        val usuario = AuthRepository.iniciarSesion("sofia@correo.cl", "Cliente123")
        assertNotNull(usuario)
        assertEquals(usuario, AuthRepository.sesion.value)
    }

    @Test
    fun loginConContrasenaIncorrectaDevuelveNull() {
        assertNull(AuthRepository.iniciarSesion("sofia@correo.cl", "otra"))
        assertNull(AuthRepository.sesion.value)
    }
}
