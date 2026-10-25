package com.aleyleo.app.viewmodel

import androidx.lifecycle.ViewModel
import com.aleyleo.app.model.Pedido
import com.aleyleo.app.model.ResultadoPedido
import com.aleyleo.app.repository.PedidoRepository
import kotlinx.coroutines.flow.StateFlow

// ViewModel de las pantallas de pedidos del cliente: Pago, Confirmación,
// Mis pedidos y Detalle del pedido (AP-10 completa las dos últimas).
class PedidosViewModel : ViewModel() {

    private val repository = PedidoRepository

    // Todos los pedidos: las pantallas buscan el suyo por id y se actualizan solas al cambiar el estado.
    val pedidos: StateFlow<List<Pedido>> = repository.pedidos

    fun pagar(pedidoId: Int): ResultadoPedido {
        return repository.pagar(pedidoId)
    }

    fun cancelar(pedidoId: Int): ResultadoPedido {
        return repository.cancelar(pedidoId)
    }
}
