package com.aleyleo.app.view.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.view.componentes.CampoTexto
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.viewmodel.RegistroViewModel

// P03 Registro: crea una cuenta de CLIENTE y entra al Catálogo con la sesión iniciada (HU-01).
@Composable
fun RegistroScreen(
    onRegistrado: () -> Unit,
    onVolver: () -> Unit,
    registroViewModel: RegistroViewModel = viewModel()
) {
    val f by registroViewModel.formulario.collectAsStateWithLifecycle()
    val e = f.errores

    PantallaBase(titulo = "Crear cuenta", onVolver = onVolver) {

        CampoTexto(f.rut, registroViewModel::cambiarRut, "RUT (ej: 12.345.678-5)", e.rut)
        CampoTexto(f.nombre, registroViewModel::cambiarNombre, "Nombre completo", e.nombre)
        CampoTexto(f.email, registroViewModel::cambiarEmail, "Email", e.email, KeyboardType.Email)
        CampoTexto(f.telefono, registroViewModel::cambiarTelefono, "Teléfono (ej: +56912345678)", e.telefono, KeyboardType.Phone)
        // El botón "Usar mi ubicación" (GPS) llega en la entrega final (HU-18).
        CampoTexto(f.direccion, registroViewModel::cambiarDireccion, "Dirección", e.direccion)
        CampoTexto(f.comuna, registroViewModel::cambiarComuna, "Comuna", e.comuna)
        CampoTexto(f.password, registroViewModel::cambiarPassword, "Contraseña (mínimo 8 caracteres)", e.password, esPassword = true)
        CampoTexto(f.confirmacion, registroViewModel::cambiarConfirmacion, "Repite la contraseña", e.confirmacion, esPassword = true)

        // Email o RUT ya registrados (en el backend: 409 Conflict).
        f.errorGeneral?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = { if (registroViewModel.registrar()) onRegistrado() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear cuenta")
        }
    }
}
