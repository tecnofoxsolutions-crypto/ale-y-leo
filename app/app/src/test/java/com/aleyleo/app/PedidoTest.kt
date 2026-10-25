package com.aleyleo.app

import com.aleyleo.app.model.EstadoPedido
import com.aleyleo.app.model.ItemSolicitud
import com.aleyleo.app.model.ResultadoPedido
import com.aleyleo.app.repository.CatalogoRepository
import com.aleyleo.app.repository.PedidoRepository
import com.aleyleo.app.viewmodel.validarCheckout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Reglas de HU-10 (generar pedido) y HU-11 (pagar), que en la final aplica el backend.
class PedidoTest {

    @Before
    @After
    fun reiniciar() {
        CatalogoRepository.reiniciar()
        PedidoRepository.reiniciar()
    }

    private fun crear(vararg items: ItemSolicitud) =
        PedidoRepository.crearPedido(2, items.toList(), "Av. Siempre Viva 123", "Providencia", "")

    @Test
    fun crearPedidoQuedaGeneradaConCodigoYTotalDelCatalogo() {
        val resultado = crear(ItemSolicitud(6, 2)) // Paila de Cobre, $14.000

        assertTrue(resultado is ResultadoPedido.Exito)
        val pedido = (resultado as ResultadoPedido.Exito).pedido
        assertEquals("PED-2026-0008", pedido.codigo)
        assertEquals(EstadoPedido.GENERADA, pedido.estado)
        assertEquals(28000, pedido.total)
        assertEquals(5, CatalogoRepository.obtenerArtesania(6)!!.stock) // aún no descuenta
    }

    @Test
    fun sinStockNoCreaElPedidoEIndicaElProducto() {
        val resultado = crear(ItemSolicitud(1, 2)) // Colgante tiene stock 1

        assertTrue(resultado is ResultadoPedido.Error)
        assertTrue((resultado as ResultadoPedido.Error).mensaje.contains("Colgante"))
        assertEquals(7, PedidoRepository.pedidos.value.size)
    }

    @Test
    fun pagarPasaAPagadaYDescuentaStock() {
        val pedido = (crear(ItemSolicitud(6, 2)) as ResultadoPedido.Exito).pedido

        val resultado = PedidoRepository.pagar(pedido.id)

        assertTrue(resultado is ResultadoPedido.Exito)
        assertEquals(EstadoPedido.PAGADA, PedidoRepository.obtenerPedido(pedido.id)!!.estado)
        assertEquals(3, CatalogoRepository.obtenerArtesania(6)!!.stock)
    }

    @Test
    fun pagarSinStockDaErrorYElPedidoSigueGenerada() {
        // Otro pedido compra el único Colgante...
        val otro = (crear(ItemSolicitud(1, 1)) as ResultadoPedido.Exito).pedido
        PedidoRepository.pagar(otro.id)

        // ...y el pedido 7 (Generada, con 1 Colgante y 2 Pocillos) ya no se puede pagar.
        val resultado = PedidoRepository.pagar(7)

        assertTrue(resultado is ResultadoPedido.Error)
        assertEquals(EstadoPedido.GENERADA, PedidoRepository.obtenerPedido(7)!!.estado)
        assertEquals(4, CatalogoRepository.obtenerArtesania(14)!!.stock) // Pocillos sin descontar
    }

    @Test
    fun cancelarSoloDesdeGeneradaYDespuesNoSePuedePagar() {
        assertTrue(PedidoRepository.cancelar(7) is ResultadoPedido.Exito)
        assertEquals(EstadoPedido.CANCELADA, PedidoRepository.obtenerPedido(7)!!.estado)
        assertTrue(PedidoRepository.pagar(7) is ResultadoPedido.Error)
        assertTrue(PedidoRepository.cancelar(1) is ResultadoPedido.Error) // la 1 está Finalizada
    }

    @Test
    fun direccionYComunaSonObligatorias() {
        assertTrue(validarCheckout("", "Providencia").hayErrores)
        assertTrue(validarCheckout("Av. Siempre Viva 123", "   ").hayErrores)
        assertFalse(validarCheckout("Av. Siempre Viva 123", "Providencia").hayErrores)
    }
}
