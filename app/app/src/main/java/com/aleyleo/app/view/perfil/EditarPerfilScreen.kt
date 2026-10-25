package com.aleyleo.app.view.perfil

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.view.componentes.CampoTexto
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.viewmodel.PerfilViewModel

// P14 Editar perfil: email y RUT se muestran pero no se pueden cambiar (HU-03).
@Composable
fun EditarPerfilScreen(
    onVolver: () -> Unit,
    perfilViewModel: PerfilViewModel = viewModel()
) {
    val formulario by perfilViewModel.formulario.collectAsStateWithLifecycle()
    val usuario by perfilViewModel.sesion.collectAsStateWithLifecycle()

    PantallaBase(titulo = "Editar perfil", onVolver = onVolver) {

        // enabled = false: se ve el dato, pero no se puede editar.
        OutlinedTextField(
            value = usuario?.email ?: "",
            onValueChange = {},
            label = { Text("Email (no se puede cambiar)") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = usuario?.rut ?: "",
            onValueChange = {},
            label = { Text("RUT (no se puede cambiar)") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        CampoTexto(
            valor = formulario.nombreCompleto,
            onCambiar = perfilViewModel::cambiarNombre,
            etiqueta = "Nombre completo",
            error = formulario.errorNombre
        )
        CampoTexto(
            valor = formulario.telefono,
            onCambiar = perfilViewModel::cambiarTelefono,
            etiqueta = "Teléfono",
            error = formulario.errorTelefono,
            tipoTeclado = KeyboardType.Phone
        )
        CampoTexto(
            valor = formulario.direccion,
            onCambiar = perfilViewModel::cambiarDireccion,
            etiqueta = "Dirección",
            error = formulario.errorDireccion
        )
        CampoTexto(
            valor = formulario.comuna,
            onCambiar = perfilViewModel::cambiarComuna,
            etiqueta = "Comuna",
            error = formulario.errorComuna
        )

        Button(
            onClick = { if (perfilViewModel.guardar()) onVolver() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
}
