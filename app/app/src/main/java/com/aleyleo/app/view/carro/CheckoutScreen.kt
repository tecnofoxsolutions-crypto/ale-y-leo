package com.aleyleo.app.view.carro

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aleyleo.app.view.componentes.AvisoEnConstruccion
import com.aleyleo.app.view.componentes.PantallaBase

// P08 Checkout
@Composable
fun CheckoutScreen(
    onVolver: () -> Unit,
    onPedidoGenerado: (Int) -> Unit
) {
    PantallaBase(titulo = "Confirmar pedido", onVolver = onVolver) {
        AvisoEnConstruccion("P08", "AP-09")

        // Por ahora usa el pedido 7 de DatosPrueba (estado GENERADA).
        Button(onClick = { onPedidoGenerado(7) }, modifier = Modifier.fillMaxWidth()) {
            Text("Generar pedido")
        }
    }
}
