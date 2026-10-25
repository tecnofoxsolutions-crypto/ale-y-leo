package com.aleyleo.app.model

// Respuesta de una operación sobre un pedido: salió bien (con el pedido) o falló (con el motivo).
// sealed class: solo existen estos dos casos, y el "when" obliga a manejar ambos.
sealed class ResultadoPedido {
    data class Exito(val pedido: Pedido) : ResultadoPedido()
    data class Error(val mensaje: String) : ResultadoPedido()
}

// Lo que la app envía para crear un pedido: solo id y cantidad.
// El precio y el nombre los pone el "servidor" (igual que POST /api/pedidos).
data class ItemSolicitud(
    val artesaniaId: Int,
    val cantidad: Int
)
