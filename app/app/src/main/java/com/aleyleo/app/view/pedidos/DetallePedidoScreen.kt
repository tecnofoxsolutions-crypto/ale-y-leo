package com.aleyleo.app.view.pedidos

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.model.EstadoPedido
import com.aleyleo.app.model.ResultadoPedido
import com.aleyleo.app.util.formatoFecha
import com.aleyleo.app.view.componentes.ChipEstado
import com.aleyleo.app.view.componentes.EstadoVacio
import com.aleyleo.app.view.componentes.LineaResumen
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.view.componentes.ResumenCompra
import com.aleyleo.app.viewmodel.PedidosViewModel

// P12 Detalle de pedido (cliente). "Pagar" y "Cancelar" solo aparecen si está GENERADA (HU-13).
@Composable
fun DetallePedidoScreen(
    pedidoId: Int,
    onVolver: () -> Unit,
    onPagar: (Int) -> Unit,
    pedidosViewModel: PedidosViewModel = viewModel()
) {
    val pedidos by pedidosViewModel.pedidos.collectAsStateWithLifecycle()
    val sesion by pedidosViewModel.sesion.collectAsStateWithLifecycle()

    // Un cliente solo ve sus propios pedidos (en el backend: 403 si es ajeno).
    val pedido = pedidos.find { it.id == pedidoId && it.usuarioId == sesion?.id }

    var confirmarCancelar by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    PantallaBase(titulo = "Detalle del pedido", onVolver = onVolver) {
        if (pedido == null) {
            EstadoVacio(mensaje = "No encontramos este pedido")
            return@PantallaBase
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(pedido.codigo, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            ChipEstado(pedido.estado)
        }
        Text(
            text = "Creado el ${formatoFecha(pedido.fechaCreacion)}",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ResumenCompra(
            lineas = pedido.detalles.map { LineaResumen(it.nombreArtesania, it.cantidad, it.subtotal) },
            total = pedido.total
        )

        Text("Despacho", style = MaterialTheme.typography.titleSmall)
        Text("${pedido.direccionDespacho}, ${pedido.comunaDespacho}")

        Text("Notas", style = MaterialTheme.typography.titleSmall)
        Text(pedido.notas.ifBlank { "Sin notas" })

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        // Solo un pedido GENERADA se puede pagar o cancelar.
        if (pedido.estado == EstadoPedido.GENERADA) {
            Button(onClick = { onPagar(pedido.id) }, modifier = Modifier.fillMaxWidth()) {
                Text("Pagar")
            }
            OutlinedButton(onClick = { confirmarCancelar = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar pedido")
            }
        }
    }

    if (confirmarCancelar && pedido != null) {
        AlertDialog(
            onDismissRequest = { confirmarCancelar = false },
            title = { Text("¿Cancelar el pedido?") },
            text = { Text("El pedido ${pedido.codigo} quedará Cancelado y no se podrá pagar.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarCancelar = false
                    val resultado = pedidosViewModel.cancelar(pedido.id)
                    if (resultado is ResultadoPedido.Error) error = resultado.mensaje
                    // Si salió bien, la pantalla se redibuja sola con el estado CANCELADA.
                }) { Text("Sí, cancelar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarCancelar = false }) { Text("Volver") }
            }
        )
    }
}
