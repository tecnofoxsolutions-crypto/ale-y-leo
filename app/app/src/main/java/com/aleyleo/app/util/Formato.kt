package com.aleyleo.app.util

import java.util.Locale

// Formatea un monto en pesos chilenos: 28500 -> "$28.500".
fun formatoClp(monto: Int): String {
    val conComas = String.format(Locale.US, "%,d", monto) // "28,500"
    return "$" + conComas.replace(",", ".")
}
