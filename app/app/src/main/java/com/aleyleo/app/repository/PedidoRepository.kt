package com.aleyleo.app.repository

import com.aleyleo.app.datos.DatosPrueba
import com.aleyleo.app.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Repositorio de pedidos. En la E2 trabaja en memoria con DatosPrueba.
object PedidoRepository {

    private val _pedidos = MutableStateFlow(DatosPrueba.pedidos)
    val pedidos: StateFlow<List<Pedido>> = _pedidos.asStateFlow()

    fun obtenerPedido(id: Int): Pedido? {
        return _pedidos.value.find { it.id == id }
    }

    // Pedidos de un cliente, del más nuevo al más antiguo.
    fun pedidosDeUsuario(usuarioId: Int): List<Pedido> {
        return _pedidos.value
            .filter { it.usuarioId == usuarioId }
            .sortedByDescending { it.id }
    }
}
