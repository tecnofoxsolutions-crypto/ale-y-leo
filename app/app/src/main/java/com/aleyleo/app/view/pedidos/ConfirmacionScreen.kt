package com.aleyleo.app.view.pedidos

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.view.componentes.ChipEstado
import com.aleyleo.app.view.componentes.EstadoVacio
import com.aleyleo.app.view.componentes.LineaResumen
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.view.componentes.ResumenCompra
import com.aleyleo.app.viewmodel.PedidosViewModel

// P10 Confirmación: el pedido quedó PAGADO.
@Composable
fun ConfirmacionScreen(
    pedidoId: Int,
    onVerMisPedidos: () -> Unit,
    onSeguirComprando: () -> Unit,
    pedidosViewModel: PedidosViewModel = viewModel()
) {
    val pedidos by pedidosViewModel.pedidos.collectAsStateWithLifecycle()
    val pedido = pedidos.find { it.id == pedidoId }

    PantallaBase(titulo = "¡Pedido pagado!") {
        if (pedido == null) {
            EstadoVacio(mensaje = "No encontramos este pedido")
            return@PantallaBase
        }

        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(72.dp)
                .align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Gracias por tu compra",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Pedido ${pedido.codigo}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        ChipEstado(pedido.estado, modifier = Modifier.align(Alignment.CenterHorizontally))

        ResumenCompra(
            lineas = pedido.detalles.map { LineaResumen(it.nombreArtesania, it.cantidad, it.subtotal) },
            total = pedido.total
        )

        Text("Despacho a: ${pedido.direccionDespacho}, ${pedido.comunaDespacho}")

        Button(onClick = onVerMisPedidos, modifier = Modifier.fillMaxWidth()) {
            Text("Ver mis pedidos")
        }
        OutlinedButton(onClick = onSeguirComprando, modifier = Modifier.fillMaxWidth()) {
            Text("Seguir comprando")
        }
    }
}
