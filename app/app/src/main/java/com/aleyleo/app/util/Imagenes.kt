package com.aleyleo.app.util

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

// Lee una imagen elegida en la galería (content://...) en tamaño reducido para mostrarla.
// Se reduce para no cargar en memoria una foto de varios MB solo para una miniatura.
fun cargarMiniatura(context: Context, uri: Uri, ladoMaximo: Int = 600): ImageBitmap? {
    return try {
        val resolver = context.contentResolver

        // 1) Solo leer el tamaño original, sin cargar la imagen.
        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, limites) }

        // 2) Cargarla dividiendo el tamaño por 2, 4, 8... hasta que quepa.
        var escala = 1
        while (limites.outWidth / escala > ladoMaximo || limites.outHeight / escala > ladoMaximo) {
            escala *= 2
        }
        val opciones = BitmapFactory.Options().apply { inSampleSize = escala }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opciones) }?.asImageBitmap()
    } catch (e: Exception) {
        null // si no se puede leer, la pantalla muestra el ícono de la categoría
    }
}

// Tamaño en bytes del archivo elegido (para validar el máximo de 5 MB).
fun tamanoArchivo(context: Context, uri: Uri): Long {
    context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst() && !cursor.isNull(0)) return cursor.getLong(0)
    }
    return 0
}
