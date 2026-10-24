package com.aleyleo.app.view.pedidos

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aleyleo.app.view.componentes.AvisoEnConstruccion
import com.aleyleo.app.view.componentes.PantallaBase

// P12 Detalle de pedido (cliente)
@Composable
fun DetallePedidoScreen(
    pedidoId: Int,
    onVolver: () -> Unit,
    onPagar: (Int) -> Unit
) {
    PantallaBase(titulo = "Detalle del pedido", onVolver = onVolver) {
        AvisoEnConstruccion("P12", "AP-10")
        Text("Pedido recibido: id $pedidoId")

        // En AP-10 este botón solo aparecerá si el pedido está GENERADA.
        Button(onClick = { onPagar(pedidoId) }, modifier = Modifier.fillMaxWidth()) {
            Text("Pagar")
        }
    }
}
