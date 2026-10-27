package com.aleyleo.app

import com.aleyleo.app.model.EstadoPedido
import com.aleyleo.app.model.ResultadoPedido
import com.aleyleo.app.repository.CatalogoRepository
import com.aleyleo.app.repository.PedidoRepository
import com.aleyleo.app.viewmodel.contarPorEstado
import com.aleyleo.app.viewmodel.filtrarPedidosAdmin
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Reglas de HU-14 (lista de la admin) y HU-15 (cambio de estado, tabla 5.3).
class AdminPedidoTest {

    @Before
    @After
    fun reiniciar() {
        CatalogoRepository.reiniciar()
        PedidoRepository.reiniciar()
    }

    @Test
    fun soloSeOfrecenLasTransicionesDeLaTabla() {
        assertEquals(listOf(EstadoPedido.RECIBIDA, EstadoPedido.RECHAZADA), EstadoPedido.PAGADA.siguientesParaAdmin())
        assertEquals(listOf(EstadoPedido.EN_PREPARACION, EstadoPedido.RECHAZADA), EstadoPedido.RECIBIDA.siguientesParaAdmin())
        assertEquals(listOf(EstadoPedido.FINALIZADA), EstadoPedido.EN_PREPARACION.siguientesParaAdmin())
        // GENERADA es del cliente; los estados finales no tienen salida.
        listOf(EstadoPedido.GENERADA, EstadoPedido.FINALIZADA, EstadoPedido.CANCELADA, EstadoPedido.RECHAZADA)
            .forEach { assertTrue(it.siguientesParaAdmin().isEmpty()) }
    }

    @Test
    fun recorridoCompletoHastaFinalizada() {
        // El pedido 4 está PAGADA.
        assertTrue(PedidoRepository.cambiarEstadoAdmin(4, EstadoPedido.RECIBIDA) is ResultadoPedido.Exito)
        assertTrue(PedidoRepository.cambiarEstadoAdmin(4, EstadoPedido.EN_PREPARACION) is ResultadoPedido.Exito)
        assertTrue(PedidoRepository.cambiarEstadoAdmin(4, EstadoPedido.FINALIZADA) is ResultadoPedido.Exito)
        assertEquals(EstadoPedido.FINALIZADA, PedidoRepository.obtenerPedido(4)!!.estado)
    }

    @Test
    fun transicionInvalidaDaErrorYNoCambiaNada() {
        // PAGADA no puede saltar a FINALIZADA, y la admin no puede pagar un pedido GENERADA.
        assertTrue(PedidoRepository.cambiarEstadoAdmin(4, EstadoPedido.FINALIZADA) is ResultadoPedido.Error)
        assertTrue(PedidoRepository.cambiarEstadoAdmin(7, EstadoPedido.PAGADA) is ResultadoPedido.Error)
        assertEquals(EstadoPedido.PAGADA, PedidoRepository.obtenerPedido(4)!!.estado)
        assertEquals(EstadoPedido.GENERADA, PedidoRepository.obtenerPedido(7)!!.estado)
    }

    @Test
    fun rechazarReponeElStock() {
        // El pedido 3 (RECIBIDA) tiene 1 Aros Lapislázuli Gota (id 2) y 2 Chanchitos (id 11).
        val arosAntes = CatalogoRepository.obtenerArtesania(2)!!.stock
        val chanchitosAntes = CatalogoRepository.obtenerArtesania(11)!!.stock

        assertTrue(PedidoRepository.cambiarEstadoAdmin(3, EstadoPedido.RECHAZADA) is ResultadoPedido.Exito)

        assertEquals(EstadoPedido.RECHAZADA, PedidoRepository.obtenerPedido(3)!!.estado)
        assertEquals(arosAntes + 1, CatalogoRepository.obtenerArtesania(2)!!.stock)
        assertEquals(chanchitosAntes + 2, CatalogoRepository.obtenerArtesania(11)!!.stock)
    }

    @Test
    fun contadoresYFiltroDeLaLista() {
        val pedidos = PedidoRepository.pedidos.value

        val contadores = contarPorEstado(pedidos)
        assertEquals(7, contadores.values.sum())
        assertEquals(1, contadores[EstadoPedido.PAGADA])

        // Todos, del más nuevo al más antiguo.
        assertEquals(listOf(7, 6, 5, 4, 3, 2, 1), filtrarPedidosAdmin(pedidos, null).map { it.id })
        assertEquals(listOf(4), filtrarPedidosAdmin(pedidos, EstadoPedido.PAGADA).map { it.id })
    }

    @Test
    fun elCambioSeVeEnLosContadores() {
        PedidoRepository.cambiarEstadoAdmin(4, EstadoPedido.RECIBIDA)

        val contadores = contarPorEstado(PedidoRepository.pedidos.value)
        assertEquals(0, contadores[EstadoPedido.PAGADA])
        assertEquals(2, contadores[EstadoPedido.RECIBIDA])
    }
}
