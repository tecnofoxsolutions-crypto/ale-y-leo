package com.aleyleo.app.view.catalogo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.model.Categoria
import com.aleyleo.app.view.componentes.EstadoVacio
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.view.componentes.TarjetaArtesania
import com.aleyleo.app.viewmodel.CatalogoViewModel

// P04 Catálogo: buscador, chips de categoría y grilla de artesanías.
@Composable
fun CatalogoScreen(
    onVerDetalle: (Int) -> Unit,
    catalogoViewModel: CatalogoViewModel = viewModel()
) {
    val uiState by catalogoViewModel.uiState.collectAsStateWithLifecycle()
    val busqueda by catalogoViewModel.busqueda.collectAsStateWithLifecycle()

    // Al cambiar el chip o la búsqueda, la grilla vuelve al principio.
    val estadoGrilla = rememberLazyGridState()
    LaunchedEffect(uiState.categoriaSeleccionada, busqueda) {
        estadoGrilla.scrollToItem(0)
    }

    PantallaBase(titulo = "Catálogo", desplazable = false) {

        Buscador(
            texto = busqueda,
            onTextoChange = { catalogoViewModel.cambiarBusqueda(it) }
        )

        ChipsCategorias(
            categorias = uiState.categorias,
            seleccionada = uiState.categoriaSeleccionada,
            onSeleccionar = { catalogoViewModel.seleccionarCategoria(it) }
        )

        if (uiState.artesanias.isEmpty()) {
            EstadoVacio(mensaje = "No encontramos artesanías con ese filtro")
        } else {
            // Grilla de 2 columnas que solo dibuja las tarjetas visibles (lazy).
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = estadoGrilla,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.artesanias, key = { it.id }) { artesania ->
                    TarjetaArtesania(
                        artesania = artesania,
                        esFavorito = false,      // AP-08 conecta los favoritos con Room
                        onClick = { onVerDetalle(artesania.id) },
                        onFavoritoClick = { }    // AP-08
                    )
                }
            }
        }
    }
}

@Composable
private fun Buscador(texto: String, onTextoChange: (String) -> Unit) {
    OutlinedTextField(
        value = texto,
        onValueChange = onTextoChange,
        placeholder = { Text("Buscar por nombre o material") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (texto.isNotEmpty()) {
                IconButton(onClick = { onTextoChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Borrar búsqueda")
                }
            }
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

// Fila deslizable con el chip "Todas" + un chip por categoría.
@Composable
private fun ChipsCategorias(
    categorias: List<Categoria>,
    seleccionada: Int?,
    onSeleccionar: (Int?) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            FilterChip(
                selected = seleccionada == null,
                onClick = { onSeleccionar(null) },
                label = { Text("Todas") }
            )
        }
        items(categorias, key = { it.id }) { categoria ->
            FilterChip(
                selected = seleccionada == categoria.id,
                onClick = { onSeleccionar(categoria.id) },
                label = { Text(categoria.nombre) }
            )
        }
    }
}
