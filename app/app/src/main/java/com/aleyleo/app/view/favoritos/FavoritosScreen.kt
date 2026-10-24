package com.aleyleo.app.view.favoritos

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aleyleo.app.view.componentes.AvisoEnConstruccion
import com.aleyleo.app.view.componentes.PantallaBase

// P06 Favoritos
@Composable
fun FavoritosScreen(
    onVerDetalle: (Int) -> Unit
) {
    PantallaBase(titulo = "Favoritos") {
        AvisoEnConstruccion("P06", "AP-08")

        OutlinedButton(onClick = { onVerDetalle(16) }, modifier = Modifier.fillMaxWidth()) {
            Text("Ver favorito de ejemplo (artesanía 16)")
        }
    }
}
