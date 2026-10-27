package com.aleyleo.app.view.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.model.Categoria
import com.aleyleo.app.model.ResultadoCatalogo
import com.aleyleo.app.view.componentes.CampoTexto
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.viewmodel.AdminCategoriasViewModel
import com.aleyleo.app.viewmodel.FilaCategoria
import kotlinx.coroutines.launch

// P19 Admin: categorías. Crear y editar en un diálogo; desactivar con su regla (HU-19).
@Composable
fun AdminCategoriasScreen(
    adminCategoriasViewModel: AdminCategoriasViewModel = viewModel()
) {
    val filas by adminCategoriasViewModel.filas.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // null = diálogo cerrado; Categoria con id 0 = nueva; otra = editar esa.
    var enDialogo by remember { mutableStateOf<Categoria?>(null) }

    fun avisar(mensaje: String, largo: Boolean = false) {
        scope.launch {
            snackbarHostState.showSnackbar(
                mensaje,
                duration = if (largo) SnackbarDuration.Long else SnackbarDuration.Short
            )
        }
    }

    PantallaBase(
        titulo = "Categorías",
        desplazable = false,
        snackbarHostState = snackbarHostState,
        botonFlotante = {
            ExtendedFloatingActionButton(
                onClick = { enDialogo = Categoria(0, "") },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nueva") }
            )
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            items(filas, key = { it.categoria.id }) { fila ->
                FilaCategoriaCard(
                    fila = fila,
                    onEditar = { enDialogo = fila.categoria },
                    onCambiarActivo = {
                        when (val resultado = adminCategoriasViewModel.cambiarActivo(fila.categoria)) {
                            // Si tiene artesanías activas no se desactiva y se explica el motivo (409).
                            is ResultadoCatalogo.Error -> avisar(resultado.mensaje, largo = true)
                            is ResultadoCatalogo.Exito -> avisar(
                                if (fila.categoria.activo) "Categoría desactivada" else "Categoría activada"
                            )
                        }
                    }
                )
            }
        }
    }

    enDialogo?.let { categoria ->
        DialogoCategoria(
            categoria = categoria,
            onCerrar = { enDialogo = null },
            onGuardar = { nombre, descripcion ->
                val resultado = adminCategoriasViewModel.guardar(categoria.id, nombre, descripcion)
                if (resultado is ResultadoCatalogo.Exito) {
                    enDialogo = null
                    avisar(if (categoria.id == 0) "Categoría creada" else "Categoría actualizada")
                    null
                } else {
                    (resultado as ResultadoCatalogo.Error).mensaje // se muestra dentro del diálogo
                }
            }
        )
    }
}

@Composable
private fun FilaCategoriaCard(fila: FilaCategoria, onEditar: () -> Unit, onCambiarActivo: () -> Unit) {
    val categoria = fila.categoria
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 8.dp, bottom = 4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
                Text(categoria.nombre, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                if (!categoria.activo) EtiquetaInactiva()
            }
            if (categoria.descripcion.isNotBlank()) {
                Text(categoria.descripcion, style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                text = "${fila.artesaniasActivas} artesanía(s) activa(s)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEditar) { Text("Editar") }
                TextButton(onClick = onCambiarActivo) {
                    Text(
                        text = if (categoria.activo) "Desactivar" else "Activar",
                        color = if (categoria.activo) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// Diálogo para crear o editar. onGuardar devuelve el mensaje de error, o null si se guardó.
@Composable
private fun DialogoCategoria(
    categoria: Categoria,
    onCerrar: () -> Unit,
    onGuardar: (nombre: String, descripcion: String) -> String?
) {
    var nombre by remember { mutableStateOf(categoria.nombre) }
    var descripcion by remember { mutableStateOf(categoria.descripcion) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(if (categoria.id == 0) "Nueva categoría" else "Editar categoría") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                CampoTexto(nombre, { nombre = it; error = null }, "Nombre", error)
                CampoTexto(descripcion, { descripcion = it }, "Descripción (opcional)", null, lineas = 2)
            }
        },
        confirmButton = {
            TextButton(onClick = { error = onGuardar(nombre, descripcion) }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) { Text("Cancelar") }
        }
    )
}
