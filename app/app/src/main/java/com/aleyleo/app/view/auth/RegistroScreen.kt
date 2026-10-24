package com.aleyleo.app.view.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aleyleo.app.view.componentes.AvisoEnConstruccion
import com.aleyleo.app.view.componentes.PantallaBase

// P03 Registro
@Composable
fun RegistroScreen(
    onRegistrado: () -> Unit,
    onVolver: () -> Unit
) {
    PantallaBase(titulo = "Crear cuenta", onVolver = onVolver) {
        AvisoEnConstruccion("P03", "AP-06")

        Button(onClick = onRegistrado, modifier = Modifier.fillMaxWidth()) {
            Text("Crear cuenta")
        }
    }
}
