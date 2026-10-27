package com.aleyleo.app.view.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.model.Artesania
import com.aleyleo.app.util.formatoClp
import com.aleyleo.app.view.componentes.EstadoVacio
import com.aleyleo.app.view.componentes.ImagenArtesania
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.viewmodel.AdminProductosViewModel

// P17 Admin: todas las artesanías (también las inactivas) con precio y stock (HU-16).
@Composable
fun AdminProductosScreen(
    onNuevo: () -> Unit,
    onEditar: (Int) -> Unit,
    adminProductosViewModel: AdminProductosViewModel = viewModel()
) {
    val productos by adminProductosViewModel.productos.collectAsStateWithLifecycle()
    val busqueda by adminProductosViewModel.busqueda.collectAsStateWithLifecycle()

    PantallaBase(
        titulo = "Productos",
        desplazable = false,
        botonFlotante = {
            ExtendedFloatingActionButton(
                onClick = onNuevo,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nuevo") }
            )
        }
    ) {
        OutlinedTextField(
            value = busqueda,
            onValueChange = adminProductosViewModel::cambiarBusqueda,
            placeholder = { Text("Buscar por nombre o material") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (busqueda.isNotEmpty()) {
                    IconButton(onClick = { adminProductosViewModel.cambiarBusqueda("") }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Borrar búsqueda")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (productos.isEmpty()) {
            EstadoVacio(mensaje = "No hay artesanías que coincidan")
        } else {
            // Espacio abajo para que el botón "Nuevo" no tape la última fila.
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                items(productos, key = { it.id }) { artesania ->
                    FilaProducto(
                        artesania = artesania,
                        categoria = adminProductosViewModel.nombreCategoria(artesania.categoriaId),
                        onClick = { onEditar(artesania.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilaProducto(artesania: Artesania, categoria: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ImagenArtesania(
                categoriaId = artesania.categoriaId,
                imagenUrl = artesania.imagenUrl,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(artesania.nombre, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "$categoria · Stock ${artesania.stock}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (artesania.agotada) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatoClp(artesania.precioBase),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (!artesania.activo) {
                EtiquetaInactiva()
            }
        }
    }
}

// Etiqueta gris para lo que está desactivado (oculto para el cliente).
@Composable
fun EtiquetaInactiva() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = "Inactiva",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
