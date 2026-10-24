package com.aleyleo.app.view.perfil

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aleyleo.app.view.componentes.AvisoEnConstruccion
import com.aleyleo.app.view.componentes.PantallaBase

// P13 Perfil (la usan el cliente y la admin)
@Composable
fun PerfilScreen(
    onEditar: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    PantallaBase(titulo = "Perfil") {
        AvisoEnConstruccion("P13", "AP-10")

        Button(onClick = onEditar, modifier = Modifier.fillMaxWidth()) {
            Text("Editar perfil")
        }
        OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar sesión")
        }
    }
}
