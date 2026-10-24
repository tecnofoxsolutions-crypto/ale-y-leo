package com.aleyleo.app.view.pedidos

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aleyleo.app.view.componentes.AvisoEnConstruccion
import com.aleyleo.app.view.componentes.PantallaBase

// P10 Confirmación del pago
@Composable
fun ConfirmacionScreen(
    pedidoId: Int,
    onVerMisPedidos: () -> Unit,
    onSeguirComprando: () -> Unit
) {
    PantallaBase(titulo = "¡Pedido pagado!") {
        AvisoEnConstruccion("P10", "AP-09")
        Text("Pedido recibido: id $pedidoId")

        Button(onClick = onVerMisPedidos, modifier = Modifier.fillMaxWidth()) {
            Text("Ver mis pedidos")
        }
        OutlinedButton(onClick = onSeguirComprando, modifier = Modifier.fillMaxWidth()) {
            Text("Seguir comprando")
        }
    }
}
