package com.aleyleo.app.view.admin

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aleyleo.app.view.componentes.AvisoEnConstruccion
import com.aleyleo.app.view.componentes.PantallaBase

// P17 Admin: lista de productos
@Composable
fun AdminProductosScreen(
    onNuevo: () -> Unit,
    onEditar: (Int) -> Unit
) {
    PantallaBase(titulo = "Productos") {
        AvisoEnConstruccion("P17", "AP-12")

        Button(onClick = onNuevo, modifier = Modifier.fillMaxWidth()) {
            Text("Nuevo producto")
        }
        OutlinedButton(onClick = { onEditar(6) }, modifier = Modifier.fillMaxWidth()) {
            Text("Editar artesanía 6")
        }
    }
}
