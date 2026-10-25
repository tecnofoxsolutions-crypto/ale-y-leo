package com.aleyleo.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleyleo.app.model.Pedido
import com.aleyleo.app.model.ResultadoPedido
import com.aleyleo.app.model.Usuario
import com.aleyleo.app.repository.AuthRepository
import com.aleyleo.app.repository.PedidoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// ViewModel de las pantallas de pedidos del cliente: Pago, Confirmación,
// Mis pedidos y Detalle del pedido.
class PedidosViewModel : ViewModel() {

    private val repository = PedidoRepository

    // Todos los pedidos: las pantallas buscan el suyo por id y se actualizan solas al cambiar el estado.
    val pedidos: StateFlow<List<Pedido>> = repository.pedidos

    val sesion: StateFlow<Usuario?> = AuthRepository.sesion

    // Solo los pedidos del cliente conectado, del más nuevo al más antiguo (HU-12).
    val misPedidos: StateFlow<List<Pedido>> = combine(repository.pedidos, AuthRepository.sesion) { pedidos, usuario ->
        filtrarMisPedidos(pedidos, usuario?.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun pagar(pedidoId: Int): ResultadoPedido {
        return repository.pagar(pedidoId)
    }

    fun cancelar(pedidoId: Int): ResultadoPedido {
        return repository.cancelar(pedidoId)
    }
}

fun filtrarMisPedidos(pedidos: List<Pedido>, usuarioId: Int?): List<Pedido> {
    if (usuarioId == null) return emptyList()
    return pedidos
        .filter { it.usuarioId == usuarioId }
        .sortedByDescending { it.id }
}
