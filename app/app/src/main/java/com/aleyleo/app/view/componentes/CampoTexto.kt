package com.aleyleo.app.view.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aleyleo.app.ui.theme.AleYLeoTheme

// Campo de texto con su mensaje de error debajo. Lo usan Login, Registro, Editar perfil y los formularios de la admin.
// esPassword = true oculta el texto y agrega el botón del ojo para mostrarlo.
@Composable
fun CampoTexto(
    valor: String,
    onCambiar: (String) -> Unit,
    etiqueta: String,
    error: String?,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    esPassword: Boolean = false,
    lineas: Int = 1 // más de 1 para textos largos, como la descripción
) {
    // Estado local de la pantalla: si la contraseña se ve o no.
    var mostrar by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = valor,
        onValueChange = onCambiar,
        label = { Text(etiqueta) },
        isError = error != null,
        supportingText = { error?.let { Text(it) } },
        singleLine = lineas == 1,
        minLines = lineas,
        keyboardOptions = KeyboardOptions(keyboardType = if (esPassword) KeyboardType.Password else tipoTeclado),
        visualTransformation = if (esPassword && !mostrar) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            if (esPassword) {
                IconButton(onClick = { mostrar = !mostrar }) {
                    Icon(
                        imageVector = if (mostrar) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (mostrar) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun CampoTextoPreview() {
    AleYLeoTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CampoTexto("sofia@correo", {}, "Email", error = "El email no tiene un formato válido")
            CampoTexto("Secreta123", {}, "Contraseña", error = null, esPassword = true)
        }
    }
}
