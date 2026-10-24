package com.aleyleo.app.view.catalogo

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aleyleo.app.view.componentes.AvisoEnConstruccion
import com.aleyleo.app.view.componentes.PantallaBase

// P04 Catálogo
@Composable
fun CatalogoScreen(
    onVerDetalle: (Int) -> Unit
) {
    PantallaBase(titulo = "Catálogo") {
        AvisoEnConstruccion("P04", "AP-07")

        OutlinedButton(onClick = { onVerDetalle(1) }, modifier = Modifier.fillMaxWidth()) {
            Text("Ver artesanía 1 (con stock)")
        }
        OutlinedButton(onClick = { onVerDetalle(3) }, modifier = Modifier.fillMaxWidth()) {
            Text("Ver artesanía 3 (agotada)")
        }
    }
}
