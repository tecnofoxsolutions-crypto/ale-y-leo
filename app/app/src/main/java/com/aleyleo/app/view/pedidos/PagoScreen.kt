package com.aleyleo.app.view.pedidos

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aleyleo.app.view.componentes.AvisoEnConstruccion
import com.aleyleo.app.view.componentes.PantallaBase

// P09 Pago simulado
@Composable
fun PagoScreen(
    pedidoId: Int,
    onPagado: (Int) -> Unit,
    onCancelado: () -> Unit
) {
    PantallaBase(titulo = "Pago") {
        AvisoEnConstruccion("P09", "AP-09")
        Text("Pedido recibido: id $pedidoId")

        Button(onClick = { onPagado(pedidoId) }, modifier = Modifier.fillMaxWidth()) {
            Text("Pagar")
        }
        OutlinedButton(onClick = onCancelado, modifier = Modifier.fillMaxWidth()) {
            Text("Cancelar pedido")
        }
    }
}
