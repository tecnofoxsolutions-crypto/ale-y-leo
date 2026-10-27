package com.aleyleo.app.util

// Formato del QR de cada artesanía: "aleyleo:artesania:<id>", por ejemplo "aleyleo:artesania:6".
// El prefijo permite reconocer los QR de la tienda y descartar cualquier otro (un link, un texto...).
const val PREFIJO_QR = "aleyleo:artesania:"

fun textoQrArtesania(id: Int): String = "$PREFIJO_QR$id"

// Devuelve el id de la artesanía, o null si el QR no es de Ale y Leo.
fun leerIdDeQr(contenido: String): Int? {
    val texto = contenido.trim()
    if (!texto.startsWith(PREFIJO_QR, ignoreCase = true)) return null
    val id = texto.substring(PREFIJO_QR.length).toIntOrNull() ?: return null
    return if (id > 0) id else null
}
