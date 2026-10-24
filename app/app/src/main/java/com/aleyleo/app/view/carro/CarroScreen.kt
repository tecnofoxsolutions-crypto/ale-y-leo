package com.aleyleo.app.view.carro

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aleyleo.app.view.componentes.AvisoEnConstruccion
import com.aleyleo.app.view.componentes.PantallaBase

// P07 Carro
@Composable
fun CarroScreen(
    onContinuar: () -> Unit
) {
    PantallaBase(titulo = "Carro") {
        AvisoEnConstruccion("P07", "AP-08")

        Button(onClick = onContinuar, modifier = Modifier.fillMaxWidth()) {
            Text("Continuar")
        }
    }
}
