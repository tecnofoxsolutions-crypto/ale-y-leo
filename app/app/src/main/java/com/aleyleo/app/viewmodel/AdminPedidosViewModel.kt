package com.aleyleo.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleyleo.app.model.EstadoPedido
import com.aleyleo.app.model.Pedido
import com.aleyleo.app.model.ResultadoPedido
import com.aleyleo.app.model.Usuario
import com.aleyleo.app.repository.AuthRepository
import com.aleyleo.app.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// Lo que muestra P15: los contadores por estado, el chip elegido y la lista ya filtrada.
data class AdminPedidosUiState(
    val contadores: Map<EstadoPedido, Int> = emptyMap(),
    val total: Int = 0,
    val estadoFiltro: EstadoPedido? = null, // null = "Todos"
    val pedidos: List<Pedido> = emptyList()
)

// ViewModel de P15 (lista de pedidos) y P16 (detalle y cambio de estado) de la admin.
class AdminPedidosViewModel : ViewModel() {

    private val repository = PedidoRepository

    private val _estadoFiltro = MutableStateFlow<EstadoPedido?>(null)

    // Se recalcula sola cuando cambia un pedido o el chip elegido (HU-14).
    val uiState: StateFlow<AdminPedidosUiState> =
        combine(repository.pedidos, _estadoFiltro) { pedidos, filtro ->
            AdminPedidosUiState(
                contadores = contarPorEstado(pedidos),
                total = pedidos.size,
                estadoFiltro = filtro,
                pedidos = filtrarPedidosAdmin(pedidos, filtro)
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminPedidosUiState())

    val pedidos: StateFlow<List<Pedido>> = repository.pedidos

    fun elegirFiltro(estado: EstadoPedido?) {
        _estadoFiltro.value = estado
    }

    fun cliente(usuarioId: Int): Usuario? = AuthRepository.obtenerUsuario(usuarioId)

    fun cambiarEstado(pedidoId: Int, nuevo: EstadoPedido): ResultadoPedido {
        return repository.cambiarEstadoAdmin(pedidoId, nuevo)
    }
}

// Cuántos pedidos hay en cada estado (incluye los estados con 0).
fun contarPorEstado(pedidos: List<Pedido>): Map<EstadoPedido, Int> {
    return EstadoPedido.entries.associateWith { estado -> pedidos.count { it.estado == estado } }
}

// Filtra por estado (null = todos) y ordena del más nuevo al más antiguo.
fun filtrarPedidosAdmin(pedidos: List<Pedido>, estado: EstadoPedido?): List<Pedido> {
    return pedidos
        .filter { estado == null || it.estado == estado }
        .sortedWith(compareByDescending<Pedido> { it.fechaCreacion }.thenByDescending { it.id })
}
