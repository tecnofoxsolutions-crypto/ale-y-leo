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
    RECHAZADA("Rechazada")
}
