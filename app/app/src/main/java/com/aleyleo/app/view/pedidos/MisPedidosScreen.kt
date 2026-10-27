package com.aleyleo.app.view.pedidos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.model.Pedido
import com.aleyleo.app.util.formatoClp
import com.aleyleo.app.util.formatoFecha
import com.aleyleo.app.view.componentes.ChipEstado
import com.aleyleo.app.view.componentes.EstadoVacio
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.viewmodel.PedidosViewModel
import com.aleyleo.app.R

// P11 Mis pedidos: lista con código, fecha, total y chip de estado (HU-12).
@Composable
fun MisPedidosScreen(
    onVerPedido: (Int) -> Unit,
    pedidosViewModel: PedidosViewModel = viewModel()
) {
    val misPedidos by pedidosViewModel.misPedidos.collectAsStateWithLifecycle()

    PantallaBase(titulo = "Mis pedidos", desplazable = false) {
        if (misPedidos.isEmpty()) {
            EstadoVacio(
                mensaje = "Aún no tienes pedidos.\nCuando compres algo, lo verás aquí.",
                ilustracion = R.drawable.ilustracion_carrete
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(misPedidos, key = { it.id }) { pedido ->
                    FilaPedido(pedido = pedido, onClick = { onVerPedido(pedido.id) })
                }
            }
        }
    }
}

// Una tarjeta de la lista de pedidos. La usa también la admin (AP-11).
@Composable
fun FilaPedido(pedido: Pedido, onClick: () -> Unit, subtitulo: String? = null) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(pedido.codigo, style = MaterialTheme.typography.titleMedium)
                if (subtitulo != null) {
                    Text(subtitulo, style = MaterialTheme.typography.bodyMedium)
                }
                Text(
                    text = formatoFecha(pedido.fechaCreacion),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatoClp(pedido.total),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            ChipEstado(pedido.estado)
        }
    }
}
