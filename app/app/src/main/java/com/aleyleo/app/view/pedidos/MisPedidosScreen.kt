package com.aleyleo.app.view.pedidos

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aleyleo.app.view.componentes.AvisoEnConstruccion
import com.aleyleo.app.view.componentes.PantallaBase

// P11 Mis pedidos
@Composable
fun MisPedidosScreen(
    onVerPedido: (Int) -> Unit
) {
    PantallaBase(titulo = "Mis pedidos") {
        AvisoEnConstruccion("P11", "AP-10")

        OutlinedButton(onClick = { onVerPedido(7) }, modifier = Modifier.fillMaxWidth()) {
            Text("Ver pedido 7 (Generada)")
        }
        OutlinedButton(onClick = { onVerPedido(1) }, modifier = Modifier.fillMaxWidth()) {
            Text("Ver pedido 1 (Finalizada)")
        }
    }
}
