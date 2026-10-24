package com.aleyleo.app.view.admin

import androidx.compose.runtime.Composable
import com.aleyleo.app.view.componentes.AvisoEnConstruccion
import com.aleyleo.app.view.componentes.PantallaBase

// P19 Admin: categorías (crear, editar y desactivar con un diálogo)
@Composable
fun AdminCategoriasScreen() {
    PantallaBase(titulo = "Categorías") {
        AvisoEnConstruccion("P19", "AP-12")
    }
}
