package com.aleyleo.app.view.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.view.componentes.AvisoEnConstruccion
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.viewmodel.AuthViewModel

// P02 Login
// Temporal hasta AP-06: los botones inician sesión con usuarios de DatosPrueba,
// para que el Checkout tenga la dirección del cliente.
@Composable
fun LoginScreen(
    onEntrarCliente: () -> Unit,
    onEntrarAdmin: () -> Unit,
    onIrARegistro: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    PantallaBase(titulo = "Iniciar sesión") {
        AvisoEnConstruccion("P02", "AP-06")

        Button(
            onClick = {
                authViewModel.iniciarSesion("sofia@correo.cl", "Cliente123")
                onEntrarCliente()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar como cliente (Sofía)")
        }
        OutlinedButton(
            onClick = {
                authViewModel.iniciarSesion("admin@aleyleo.cl", "Admin123")
                onEntrarAdmin()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar como administradora")
        }
        TextButton(onClick = onIrARegistro, modifier = Modifier.fillMaxWidth()) {
            Text("¿No tienes cuenta? Crear cuenta")
        }
    }
}
