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
import com.aleyleo.app.util.formatoClp
import com.aleyleo.app.view.componentes.ChipEstado
import com.aleyleo.app.view.componentes.EstadoVacio
import com.aleyleo.app.view.componentes.LineaResumen
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.view.componentes.ResumenCompra
import com.aleyleo.app.viewmodel.PedidosViewModel

// P09 Pago simulado: código, total y un único botón "Pagar", sin datos de tarjeta (HU-11).
@Composable
fun PagoScreen(
    pedidoId: Int,
    onPagado: (Int) -> Unit,
    onCancelado: () -> Unit,
    pedidosViewModel: PedidosViewModel = viewModel()
) {
    val pedidos by pedidosViewModel.pedidos.collectAsStateWithLifecycle()
    val pedido = pedidos.find { it.id == pedidoId }

    var error by remember { mutableStateOf<String?>(null) }
    var confirmarCancelar by remember { mutableStateOf(false) }

    if (pedido == null) {
        PantallaBase(titulo = "Pago") { EstadoVacio(mensaje = "No encontramos este pedido") }
        return
    }

    val puedePagar = pedido.estado == EstadoPedido.GENERADA

    PantallaBase(titulo = "Pago") {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = pedido.codigo,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            ChipEstado(pedido.estado)
        }

        ResumenCompra(
            lineas = pedido.detalles.map { LineaResumen(it.nombreArtesania, it.cantidad, it.subtotal) },
            total = pedido.total
        )

        Text(
            text = "Pago simulado: no se piden datos de tarjeta.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (!puedePagar) {
            Text("Este pedido está ${pedido.estado.etiqueta} y ya no se puede pagar.")
        }

        // Por ejemplo, si al pagar ya no queda stock (el pedido sigue Generada).
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = {
                when (val resultado = pedidosViewModel.pagar(pedido.id)) {
                    is ResultadoPedido.Exito -> onPagado(pedido.id)
                    is ResultadoPedido.Error -> error = resultado.mensaje
                }
            },
            enabled = puedePagar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pagar ${formatoClp(pedido.total)}")
        }

        OutlinedButton(
            onClick = { confirmarCancelar = true },
            enabled = puedePagar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar pedido")
        }
    }

    if (confirmarCancelar) {
        AlertDialog(
            onDismissRequest = { confirmarCancelar = false },
            title = { Text("¿Cancelar el pedido?") },
            text = { Text("El pedido ${pedido.codigo} quedará Cancelado y no se podrá pagar.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarCancelar = false
                    when (val resultado = pedidosViewModel.cancelar(pedido.id)) {
                        is ResultadoPedido.Exito -> onCancelado()
                        is ResultadoPedido.Error -> error = resultado.mensaje
                    }
                }) { Text("Sí, cancelar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarCancelar = false }) { Text("Volver") }
            }
        )
    }
}
