package com.aleyleo.app.view.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aleyleo.app.view.componentes.AvisoEnConstruccion
import com.aleyleo.app.view.componentes.PantallaBase

// P02 Login
@Composable
fun LoginScreen(
    onEntrarCliente: () -> Unit,
    onEntrarAdmin: () -> Unit,
    onIrARegistro: () -> Unit
) {
    PantallaBase(titulo = "Iniciar sesión") {
        AvisoEnConstruccion("P02", "AP-06")

        Button(onClick = onEntrarCliente, modifier = Modifier.fillMaxWidth()) {
            Text("Entrar como cliente")
        }
        OutlinedButton(onClick = onEntrarAdmin, modifier = Modifier.fillMaxWidth()) {
            Text("Entrar como administradora")
        }
        TextButton(onClick = onIrARegistro, modifier = Modifier.fillMaxWidth()) {
            Text("¿No tienes cuenta? Crear cuenta")
        }
    }
}
