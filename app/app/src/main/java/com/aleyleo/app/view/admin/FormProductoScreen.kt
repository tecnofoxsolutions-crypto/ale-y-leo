package com.aleyleo.app.view.admin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.model.Categoria
import com.aleyleo.app.view.componentes.CampoTexto
import com.aleyleo.app.view.componentes.ImagenArtesania
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.viewmodel.FormProductoViewModel
import kotlinx.coroutines.launch

// P18 Admin: formulario de producto. artesaniaId = 0 significa "producto nuevo" (HU-16).
@Composable
fun FormProductoScreen(
    artesaniaId: Int,
    onVolver: () -> Unit,
    formProductoViewModel: FormProductoViewModel = viewModel()
) {
    val formulario by formProductoViewModel.formulario.collectAsStateWithLifecycle()
    val categorias by formProductoViewModel.categorias.collectAsStateWithLifecycle()
    val esNuevo = artesaniaId == 0

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(artesaniaId) {
        if (!esNuevo) formProductoViewModel.cargar(artesaniaId)
    }

    // Selector de fotos de Android: no necesita pedir permisos.
    val elegirFoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) formProductoViewModel.elegirImagen(uri)
    }

    PantallaBase(
        titulo = if (esNuevo) "Nuevo producto" else "Editar producto",
        onVolver = onVolver,
        snackbarHostState = snackbarHostState
    ) {
        // Vista previa: la foto elegida o, si no hay, el ícono de la categoría.
        ImagenArtesania(
            categoriaId = formulario.categoriaId ?: 0,
            imagenUrl = formulario.imagenUrl,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        OutlinedButton(
            onClick = {
                elegirFoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
            Text("  Elegir imagen de la galería")
        }
        Text(
            text = formulario.errorImagen ?: "JPG o PNG, máximo 5 MB",
            style = MaterialTheme.typography.bodySmall,
            color = if (formulario.errorImagen != null) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurfaceVariant
        )

        CampoTexto(formulario.nombre, formProductoViewModel::cambiarNombre, "Nombre", formulario.errorNombre)

        SelectorCategoria(
            categorias = categorias.filter { it.activo || it.id == formulario.categoriaId },
            seleccionada = formulario.categoriaId,
            error = formulario.errorCategoria,
            onElegir = formProductoViewModel::cambiarCategoria
        )

        CampoTexto(
            formulario.descripcion, formProductoViewModel::cambiarDescripcion,
            "Descripción", formulario.errorDescripcion, lineas = 3
        )
        CampoTexto(
            formulario.materiales, formProductoViewModel::cambiarMateriales,
            "Materiales o técnica", formulario.errorMateriales
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.weight(1f)) {
                CampoTexto(
                    formulario.precio, formProductoViewModel::cambiarPrecio,
                    "Precio (CLP)", formulario.errorPrecio, tipoTeclado = KeyboardType.Number
                )
            }
            Row(modifier = Modifier.weight(1f)) {
                CampoTexto(
                    formulario.stock, formProductoViewModel::cambiarStock,
                    "Stock", formulario.errorStock, tipoTeclado = KeyboardType.Number
                )
            }
        }

        formulario.errorGeneral?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = { if (formProductoViewModel.guardar()) onVolver() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (esNuevo) "Crear producto" else "Guardar cambios")
        }

        // Desactivar = borrado lógico: deja de verse en el catálogo, pero no se borra.
        if (!esNuevo) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (formulario.activo) "Visible en el catálogo" else "Oculta del catálogo",
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!formulario.activo) EtiquetaInactiva()
            }
            OutlinedButton(
                onClick = {
                    val desactivando = formulario.activo
                    formProductoViewModel.cambiarActivo()
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (desactivando) "Artesanía desactivada: ya no aparece en el catálogo"
                            else "Artesanía activada: vuelve a aparecer en el catálogo"
                        )
                    }
                },
                colors = if (formulario.activo) {
                    ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                } else {
                    ButtonDefaults.outlinedButtonColors()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (formulario.activo) "Desactivar" else "Activar de nuevo")
            }
        }
    }
}

// Desplegable de categorías (ExposedDropdownMenuBox de Material 3).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorCategoria(
    categorias: List<Categoria>,
    seleccionada: Int?,
    error: String?,
    onElegir: (Int) -> Unit
) {
    var abierto by remember { mutableStateOf(false) }
    val nombre = categorias.find { it.id == seleccionada }?.nombre ?: ""

    ExposedDropdownMenuBox(expanded = abierto, onExpandedChange = { abierto = it }) {
        OutlinedTextField(
            value = nombre,
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoría") },
            isError = error != null,
            supportingText = { error?.let { Text(it) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = abierto) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            categorias.forEach { categoria ->
                DropdownMenuItem(
                    text = { Text(categoria.nombre) },
                    onClick = {
                        onElegir(categoria.id)
                        abierto = false
                    }
                )
            }
        }
    }
}
