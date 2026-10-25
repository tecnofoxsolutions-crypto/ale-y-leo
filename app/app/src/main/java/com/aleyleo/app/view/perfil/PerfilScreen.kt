package com.aleyleo.app.view.perfil

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.model.Rol
import com.aleyleo.app.view.componentes.EstadoVacio
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.viewmodel.PerfilViewModel
import kotlinx.coroutines.launch

// P13 Perfil (la usan el cliente y la admin): datos del usuario, Editar y Cerrar sesión.
@Composable
fun PerfilScreen(
    onEditar: () -> Unit,
    onCerrarSesion: () -> Unit,
    perfilViewModel: PerfilViewModel = viewModel()
) {
    val usuario by perfilViewModel.sesion.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    PantallaBase(titulo = "Perfil") {
        val u = usuario
        if (u == null) {
            EstadoVacio(mensaje = "No hay una sesión iniciada")
            return@PantallaBase
        }

        // La foto de perfil con la cámara llega en la entrega final (HU-17).
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(96.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = u.nombreCompleto,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = if (u.rol == Rol.ADMIN) "Administradora" else "Cliente",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                DatoPerfil("Email", u.email)
                HorizontalDivider()
                DatoPerfil("RUT", u.rut)
                HorizontalDivider()
                DatoPerfil("Teléfono", u.telefono)
                HorizontalDivider()
                DatoPerfil("Dirección", u.direccion)
                HorizontalDivider()
                DatoPerfil("Comuna", u.comuna)
            }
        }

        Button(onClick = onEditar, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Edit, contentDescription = null)
            Text("Editar perfil", modifier = Modifier.padding(start = 8.dp))
        }

        OutlinedButton(
            onClick = {
                // Primero vacía carro y favoritos (Room) y después navega al Login.
                scope.launch {
                    perfilViewModel.cerrarSesion()
                    onCerrarSesion()
                }
            },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Text("Cerrar sesión", modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun DatoPerfil(etiqueta: String, valor: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}
