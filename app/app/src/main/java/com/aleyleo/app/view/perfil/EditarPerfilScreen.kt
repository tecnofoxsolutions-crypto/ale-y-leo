package com.aleyleo.app.view.perfil

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aleyleo.app.view.componentes.AvisoEnConstruccion
import com.aleyleo.app.view.componentes.PantallaBase

// P14 Editar perfil
@Composable
fun EditarPerfilScreen(
    onVolver: () -> Unit
) {
    PantallaBase(titulo = "Editar perfil", onVolver = onVolver) {
        AvisoEnConstruccion("P14", "AP-10")

        Button(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("Guardar")
        }
    }
}
