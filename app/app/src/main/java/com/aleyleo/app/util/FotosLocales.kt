package com.aleyleo.app.util

import com.aleyleo.app.R

// Fotos de las 20 artesanías de prueba (DI-10, de Emanuel), guardadas en res/drawable-nodpi.
// En la E2 la app no se conecta al backend, así que la ruta "/uploads/art-6.jpg" se traduce
// a la foto local art_6. En la entrega final la foto llegará desde el servidor.
private val fotos = mapOf(
    1 to R.drawable.art_1, 2 to R.drawable.art_2, 3 to R.drawable.art_3, 4 to R.drawable.art_4,
    5 to R.drawable.art_5, 6 to R.drawable.art_6, 7 to R.drawable.art_7, 8 to R.drawable.art_8,
    9 to R.drawable.art_9, 10 to R.drawable.art_10, 11 to R.drawable.art_11, 12 to R.drawable.art_12,
    13 to R.drawable.art_13, 14 to R.drawable.art_14, 15 to R.drawable.art_15, 16 to R.drawable.art_16,
    17 to R.drawable.art_17, 18 to R.drawable.art_18, 19 to R.drawable.art_19, 20 to R.drawable.art_20
)

// Devuelve la foto local para una ruta como "/uploads/art-6.jpg", o null si no hay.
fun fotoLocal(imagenUrl: String): Int? {
    val id = Regex("""/uploads/art-(\d+)\.""").find(imagenUrl)?.groupValues?.get(1)?.toIntOrNull()
    return id?.let { fotos[it] }
}

// Ruta de la foto de una artesanía de prueba (la usa el Carro, que solo guarda el id).
fun rutaFoto(artesaniaId: Int): String = "/uploads/art-$artesaniaId.jpg"
