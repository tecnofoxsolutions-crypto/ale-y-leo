package com.aleyleo.app.view.admin

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.aleyleo.app.viewmodel.AdminPedidosViewModel
import kotlinx.coroutines.launch

// P16 Admin: detalle del pedido y cambio de estado (HU-14 y HU-15).
// Solo aparecen los botones de las transiciones válidas para el estado actual.
@Composable
fun AdminDetallePedidoScreen(
    pedidoId: Int,
    onVolver: () -> Unit,
    adminPedidosViewModel: AdminPedidosViewModel = viewModel()
) {
    val pedidos by adminPedidosViewModel.pedidos.collectAsStateWithLifecycle()
    val pedido = pedidos.find { it.id == pedidoId }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var confirmarRechazo by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun cambiar(nuevo: EstadoPedido) {
        val resultado = adminPedidosViewModel.cambiarEstado(pedidoId, nuevo)
        if (resultado is ResultadoPedido.Error) {
            error = resultado.mensaje
        } else {
            error = null
            // La pantalla se redibuja sola con el nuevo estado; el mensaje solo lo confirma.
            scope.launch { snackbarHostState.showSnackbar("El pedido pasó a ${nuevo.etiqueta}") }
        }
    }

    PantallaBase(titulo = "Detalle del pedido", onVolver = onVolver, snackbarHostState = snackbarHostState) {
        if (pedido == null) {
            EstadoVacio(mensaje = "No encontramos este pedido")
            return@PantallaBase
        }
        val cliente = adminPedidosViewModel.cliente(pedido.usuarioId)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(pedido.codigo, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            ChipEstado(pedido.estado)
        }
        Text(
            text = "Creado el ${formatoFecha(pedido.fechaCreacion)}",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text("Cliente", style = MaterialTheme.typography.titleSmall)
        if (cliente != null) {
            Text(cliente.nombreCompleto)
            Text("RUT ${cliente.rut}")
            Text(cliente.email)
            Text(cliente.telefono)
        } else {
            Text("Cliente no encontrado")
        }

        Text("Despacho", style = MaterialTheme.typography.titleSmall)
        Text("${pedido.direccionDespacho}, ${pedido.comunaDespacho}")

        ResumenCompra(
            lineas = pedido.detalles.map { LineaResumen(it.nombreArtesania, it.cantidad, it.subtotal) },
            total = pedido.total
        )

        Text("Notas", style = MaterialTheme.typography.titleSmall)
        Text(pedido.notas.ifBlank { "Sin notas" })

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        val siguientes = pedido.estado.siguientesParaAdmin()
        if (siguientes.isEmpty()) {
            Text(
                text = textoSinAcciones(pedido.estado),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        siguientes.forEach { nuevo ->
            if (nuevo == EstadoPedido.RECHAZADA) {
                OutlinedButton(
                    onClick = { confirmarRechazo = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Rechazar pedido") }
            } else {
                Button(onClick = { cambiar(nuevo) }, modifier = Modifier.fillMaxWidth()) {
                    Text(textoBoton(nuevo))
                }
            }
        }
    }

    // Rechazar no se puede deshacer y repone el stock, por eso pide confirmación.
    if (confirmarRechazo && pedido != null) {
        AlertDialog(
            onDismissRequest = { confirmarRechazo = false },
            title = { Text("¿Rechazar el pedido?") },
            text = { Text("El pedido ${pedido.codigo} quedará Rechazado y las unidades vuelven al stock.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarRechazo = false
                    cambiar(EstadoPedido.RECHAZADA)
                }) { Text("Sí, rechazar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarRechazo = false }) { Text("Volver") }
            }
        )
    }
}

private fun textoBoton(nuevo: EstadoPedido): String = when (nuevo) {
    EstadoPedido.RECIBIDA -> "Marcar como recibida"
    EstadoPedido.EN_PREPARACION -> "Pasar a En preparación"
    EstadoPedido.FINALIZADA -> "Marcar como finalizada"
    else -> "Pasar a ${nuevo.etiqueta}"
}

private fun textoSinAcciones(estado: EstadoPedido): String = when (estado) {
    EstadoPedido.GENERADA -> "Esperando el pago del cliente."
    else -> "Estado final (${estado.etiqueta}): no hay más cambios de estado."
}
