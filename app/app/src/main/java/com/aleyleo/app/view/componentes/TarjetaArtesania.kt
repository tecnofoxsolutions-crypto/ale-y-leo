package com.aleyleo.app.view.componentes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aleyleo.app.datos.DatosPrueba
import com.aleyleo.app.model.Artesania
import com.aleyleo.app.ui.theme.AleYLeoTheme
import com.aleyleo.app.util.formatoClp

// Tarjeta de la grilla del catálogo: imagen, nombre, precio y corazón de favorito.
@Composable
fun TarjetaArtesania(
    artesania: Artesania,
    esFavorito: Boolean,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(onClick = onClick, modifier = modifier) {
        Box {
            ImagenArtesania(
                categoriaId = artesania.categoriaId,
                imagenUrl = artesania.imagenUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )

            if (artesania.agotada) {
                EtiquetaAgotado(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                )
            }
        }

        Row(
            modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = artesania.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    minLines = 2, // todas las tarjetas quedan de la misma altura
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatoClp(artesania.precioBase),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            BotonFavorito(esFavorito = esFavorito, onClick = onFavoritoClick)
        }
    }
}

// Corazón lleno si es favorito, vacío si no.
@Composable
fun BotonFavorito(esFavorito: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

// Etiqueta roja "Agotado" para productos sin stock.
@Composable
fun EtiquetaAgotado(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = "Agotado",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Preview
@Composable
fun TarjetaArtesaniaPreview() {
    AleYLeoTheme {
        TarjetaArtesania(
            artesania = DatosPrueba.artesanias[0],
            esFavorito = true,
            onClick = {},
            onFavoritoClick = {},
            modifier = Modifier.width(180.dp)
        )
    }
}

@Preview
@Composable
fun TarjetaArtesaniaAgotadaPreview() {
    AleYLeoTheme {
        TarjetaArtesania(
            artesania = DatosPrueba.artesanias[2], // stock 0
            esFavorito = false,
            onClick = {},
            onFavoritoClick = {},
            modifier = Modifier.width(180.dp)
        )
    }
}
