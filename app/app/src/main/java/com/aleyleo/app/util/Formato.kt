package com.aleyleo.app.util

import java.util.Locale

// Formatea un monto en pesos chilenos: 28500 -> "$28.500".
fun formatoClp(monto: Int): String {
    val conComas = String.format(Locale.US, "%,d", monto) // "28,500"
    return "$" + conComas.replace(",", ".")
}

// Pasa la fecha guardada "2026-10-21 18:30" al formato chileno "21/10/2026 18:30".
fun formatoFecha(fecha: String): String {
    val partes = fecha.split(" ")
    val dia = partes[0].split("-")
    if (dia.size != 3) return fecha // si viene en otro formato, se muestra tal cual
    val hora = partes.getOrNull(1) ?: ""
    return "${dia[2]}/${dia[1]}/${dia[0]} $hora".trim()
}
