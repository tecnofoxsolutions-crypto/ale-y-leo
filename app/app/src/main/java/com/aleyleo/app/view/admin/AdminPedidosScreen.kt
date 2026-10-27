package com.aleyleo.app.view.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.model.EstadoPedido
import com.aleyleo.app.view.componentes.EstadoVacio
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.view.pedidos.FilaPedido
import com.aleyleo.app.viewmodel.AdminPedidosViewModel

// P15 Admin: todos los pedidos, con contadores por estado y chips para filtrar (HU-14).
@Composable
fun AdminPedidosScreen(
    onVerPedido: (Int) -> Unit,
    adminPedidosViewModel: AdminPedidosViewModel = viewModel()
) {
    val uiState by adminPedidosViewModel.uiState.collectAsStateWithLifecycle()
    val estadoLista = rememberLazyListState()

    // Al cambiar de chip, la lista vuelve arriba.
    LaunchedEffect(uiState.estadoFiltro) {
        estadoLista.scrollToItem(0)
    }

    PantallaBase(titulo = "Pedidos", desplazable = false) {
        // Cada chip es a la vez contador y filtro: "Pagada (1)".
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = uiState.estadoFiltro == null,
                    onClick = { adminPedidosViewModel.elegirFiltro(null) },
                    label = { Text("Todos (${uiState.total})") }
                )
            }
            items(EstadoPedido.entries, key = { it.name }) { estado ->
                FilterChip(
                    selected = uiState.estadoFiltro == estado,
                    onClick = { adminPedidosViewModel.elegirFiltro(estado) },
                    label = { Text("${estado.etiqueta} (${uiState.contadores[estado] ?: 0})") }
                )
            }
        }

        Text(
            text = "${uiState.pedidos.size} pedido(s)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (uiState.pedidos.isEmpty()) {
            EstadoVacio(
                mensaje = "No hay pedidos en este estado",
                icono = Icons.AutoMirrored.Filled.ReceiptLong
            )
        } else {
            LazyColumn(state = estadoLista, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.pedidos, key = { it.id }) { pedido ->
                    FilaPedido(
                        pedido = pedido,
                        onClick = { onVerPedido(pedido.id) },
                        subtitulo = adminPedidosViewModel.cliente(pedido.usuarioId)?.nombreCompleto
                    )
                }
            }
        }
    }
}
