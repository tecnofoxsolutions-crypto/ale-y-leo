package com.aleyleo.api.pedido

// Los 7 estados del pedido (sección 5.3).
enum class EstadoPedido {
    GENERADA,
    PAGADA,
    RECIBIDA,
    EN_PREPARACION,
    FINALIZADA,
    CANCELADA,
    RECHAZADA;

    // Estados a los que la ADMIN puede mover un pedido desde este estado (tabla 5.3).
    // Pagar y cancelar son del cliente y tienen su propio endpoint.
    fun siguientesParaAdmin(): List<EstadoPedido> = when (this) {
        PAGADA -> listOf(RECIBIDA, RECHAZADA)
        RECIBIDA -> listOf(EN_PREPARACION, RECHAZADA)
        EN_PREPARACION -> listOf(FINALIZADA)
        else -> emptyList() // GENERADA es del cliente; FINALIZADA, CANCELADA y RECHAZADA son finales
    }
}
