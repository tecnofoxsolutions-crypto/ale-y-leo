package com.aleyleo.app.view.admin

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aleyleo.app.view.componentes.AvisoEnConstruccion
import com.aleyleo.app.view.componentes.PantallaBase

// P18 Admin: formulario de producto. artesaniaId = 0 significa "producto nuevo".
@Composable
fun FormProductoScreen(
    artesaniaId: Int,
    onVolver: () -> Unit
) {
    val esNuevo = artesaniaId == 0

    PantallaBase(
        titulo = if (esNuevo) "Nuevo producto" else "Editar producto",
        onVolver = onVolver
    ) {
        AvisoEnConstruccion("P18", "AP-12")
        if (!esNuevo) {
            Text("Artesanía recibida: id $artesaniaId")
        }

        Button(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("Guardar")
        }
    }
}
