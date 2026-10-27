package com.aleyleo.app.view.componentes

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiNature
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aleyleo.app.ui.theme.AleYLeoTheme
import com.aleyleo.app.util.cargarMiniatura
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Imagen de la artesanía. Si la admin eligió una foto de la galería (content://...), la muestra.
// Si no, mientras no lleguen las fotos (tarjeta DI-10), un fondo de color con el ícono de su categoría.
@Composable
fun ImagenArtesania(
    categoriaId: Int,
    modifier: Modifier = Modifier,
    imagenUrl: String = ""
) {
    val context = LocalContext.current
    // produceState carga la foto fuera del hilo principal y redibuja cuando está lista.
    val foto by produceState<ImageBitmap?>(initialValue = null, imagenUrl) {
        value = if (imagenUrl.startsWith("content://")) {
            withContext(Dispatchers.IO) { cargarMiniatura(context, Uri.parse(imagenUrl)) }
        } else {
            null
        }
    }

    foto?.let {
        Image(
            bitmap = it,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
        return
    }

    val icono = when (categoriaId) {
        1 -> Icons.Filled.Diamond      // Orfebrería
        2 -> Icons.Filled.LocalCafe    // Cobre
        3 -> Icons.Filled.EmojiNature  // Alfarería
        else -> Icons.Filled.Checkroom // Textil
    }

    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Preview
@Composable
fun ImagenArtesaniaPreview() {
    AleYLeoTheme {
        ImagenArtesania(categoriaId = 2, modifier = Modifier.size(160.dp))
    }
}

@Preview
@Composable
private fun ImagenArtesaniaGrandePreview() {
    AleYLeoTheme {
        Box(Modifier.size(300.dp)) {
            ImagenArtesania(categoriaId = 1, modifier = Modifier.fillMaxSize())
        }
    }
}
