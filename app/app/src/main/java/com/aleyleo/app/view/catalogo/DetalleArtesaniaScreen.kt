package com.aleyleo.app.view.catalogo

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aleyleo.app.view.componentes.AvisoEnConstruccion
import com.aleyleo.app.view.componentes.PantallaBase

// P05 Detalle de artesanía: recibe el id desde la ruta "detalle_artesania/{artesaniaId}".
@Composable
fun DetalleArtesaniaScreen(
    artesaniaId: Int,
    onVolver: () -> Unit,
    onIrAlCarro: () -> Unit
) {
    PantallaBase(titulo = "Detalle", onVolver = onVolver) {
        AvisoEnConstruccion("P05", "AP-07")
        Text("Artesanía recibida: id $artesaniaId")

        Button(onClick = onIrAlCarro, modifier = Modifier.fillMaxWidth()) {
            Text("Agregar al carro")
        }
    }
}
