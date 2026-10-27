package com.aleyleo.app.model

// Los 7 estados del pedido (sección 5.3 del documento de arquitectura).
// "etiqueta" es el texto que se muestra en pantalla.
enum class EstadoPedido(val etiqueta: String) {
    GENERADA("Generada"),
    PAGADA("Pagada"),
    RECIBIDA("Recibida"),
    EN_PREPARACION("En preparación"),
    FINALIZADA("Finalizada"),
    CANCELADA("Cancelada"),
    RECHAZADA("Rechazada");

    // Estados a los que la ADMIN puede mover un pedido desde este estado (tabla 5.3).
    // Las demás transiciones (pagar y cancelar) son del cliente y no aparecen aquí.
    fun siguientesParaAdmin(): List<EstadoPedido> = when (this) {
        PAGADA -> listOf(RECIBIDA, RECHAZADA)
        RECIBIDA -> listOf(EN_PREPARACION, RECHAZADA)
        EN_PREPARACION -> listOf(FINALIZADA)
        else -> emptyList() // GENERADA es del cliente; FINALIZADA, CANCELADA y RECHAZADA son finales
    }
}
