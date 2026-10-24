package com.aleyleo.app.view.admin

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aleyleo.app.view.componentes.AvisoEnConstruccion
import com.aleyleo.app.view.componentes.PantallaBase

// P15 Admin: pedidos consolidados
@Composable
fun AdminPedidosScreen(
    onVerPedido: (Int) -> Unit
) {
    PantallaBase(titulo = "Pedidos") {
        AvisoEnConstruccion("P15", "AP-11")

        OutlinedButton(onClick = { onVerPedido(4) }, modifier = Modifier.fillMaxWidth()) {
            Text("Ver pedido 4 (Pagada)")
        }
    }
}
