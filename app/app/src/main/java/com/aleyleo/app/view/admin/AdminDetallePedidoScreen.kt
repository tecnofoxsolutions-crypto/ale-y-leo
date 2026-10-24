package com.aleyleo.app.view.admin

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.aleyleo.app.view.componentes.AvisoEnConstruccion
import com.aleyleo.app.view.componentes.PantallaBase

// P16 Admin: detalle de pedido y cambio de estado
@Composable
fun AdminDetallePedidoScreen(
    pedidoId: Int,
    onVolver: () -> Unit
) {
    PantallaBase(titulo = "Detalle del pedido", onVolver = onVolver) {
        AvisoEnConstruccion("P16", "AP-11")
        Text("Pedido recibido: id $pedidoId")
    }
}
