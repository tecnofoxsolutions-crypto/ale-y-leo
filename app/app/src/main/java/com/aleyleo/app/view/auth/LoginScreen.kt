package com.aleyleo.app.view.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.model.Rol
import com.aleyleo.app.view.componentes.CampoTexto
import com.aleyleo.app.view.componentes.LogoAleYLeo
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.viewmodel.AuthViewModel

// P02 Login: email y contraseña. Según el rol entra al Catálogo (cliente) o a Pedidos (admin).
@Composable
fun LoginScreen(
    onEntrarCliente: () -> Unit,
    onEntrarAdmin: () -> Unit,
    onIrARegistro: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val formulario by authViewModel.formulario.collectAsStateWithLifecycle()

    PantallaBase(titulo = "Iniciar sesión") {

        LogoAleYLeo(
            modifier = Modifier
                .size(96.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Bienvenida a Ale y Leo",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        CampoTexto(
            valor = formulario.email,
            onCambiar = authViewModel::cambiarEmail,
            etiqueta = "Email",
            error = null,
            tipoTeclado = KeyboardType.Email
        )
        CampoTexto(
            valor = formulario.password,
            onCambiar = authViewModel::cambiarPassword,
            etiqueta = "Contraseña",
            error = null,
            esPassword = true
        )

        // Un solo mensaje para todo el formulario: no revela si falló el email o la contraseña.
        formulario.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = {
                when (authViewModel.iniciarSesion()) {
                    Rol.CLIENTE -> onEntrarCliente()
                    Rol.ADMIN -> onEntrarAdmin()
                    null -> Unit // el error ya quedó en el formulario
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }

        TextButton(onClick = onIrARegistro, modifier = Modifier.fillMaxWidth()) {
            Text("¿No tienes cuenta? Crear cuenta")
        }

        UsuariosDePrueba()
    }
}

// Ayuda para probar la app y grabar el video (datos ficticios de DatosPrueba).
@Composable
private fun UsuariosDePrueba() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Usuarios de prueba", style = MaterialTheme.typography.labelLarge)
            Text("Cliente: sofia@correo.cl / Cliente123", style = MaterialTheme.typography.bodySmall)
            Text("Admin: admin@aleyleo.cl / Admin123", style = MaterialTheme.typography.bodySmall)
        }
    }
}
