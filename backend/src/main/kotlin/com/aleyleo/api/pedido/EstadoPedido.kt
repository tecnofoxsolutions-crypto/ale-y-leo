package com.aleyleo.api.pedido

// Los 7 estados del pedido (sección 5.3). Las transiciones válidas las controla PedidoService (BE-15).
enum class EstadoPedido {
    GENERADA,
    PAGADA,
    RECIBIDA,
    EN_PREPARACION,
    FINALIZADA,
    CANCELADA,
    RECHAZADA
}
