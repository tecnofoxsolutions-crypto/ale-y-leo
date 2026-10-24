package com.aleyleo.app.view.favoritos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.view.componentes.EstadoVacio
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.view.componentes.TarjetaArtesania
import com.aleyleo.app.viewmodel.FavoritosViewModel

// P06 Favoritos: las artesanías marcadas con el corazón (guardadas en Room).
@Composable
fun FavoritosScreen(
    onVerDetalle: (Int) -> Unit,
    favoritosViewModel: FavoritosViewModel = viewModel()
) {
    val favoritas by favoritosViewModel.artesaniasFavoritas.collectAsStateWithLifecycle()

    PantallaBase(titulo = "Favoritos", desplazable = false) {
        if (favoritas.isEmpty()) {
            EstadoVacio(
                mensaje = "Aún no tienes favoritos.\nToca el corazón de una artesanía para guardarla aquí.",
                icono = Icons.Filled.FavoriteBorder
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoritas, key = { it.id }) { artesania ->
                    TarjetaArtesania(
                        artesania = artesania,
                        esFavorito = true,
                        onClick = { onVerDetalle(artesania.id) },
                        // Tocar el corazón aquí la quita de favoritos.
                        onFavoritoClick = { favoritosViewModel.alternarFavorito(artesania) }
                    )
                }
            }
        }
    }
}
