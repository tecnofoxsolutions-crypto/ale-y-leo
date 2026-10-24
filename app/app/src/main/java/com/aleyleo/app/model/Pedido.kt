package com.aleyleo.app.model

// Cabecera del pedido. Guarda una copia de la dirección por si el cliente la cambia después.
data class Pedido(
    val id: Int,
    val codigo: String,
    val usuarioId: Int,
    val estado: EstadoPedido,
    val direccionDespacho: String,
    val comunaDespacho: String,
    val notas: String,
    val fechaCreacion: String,
    val detalles: List<DetallePedido>
) {
    val total: Int
        get() = detalles.sumOf { it.subtotal }
}

// Una línea del pedido. Copia el nombre y el precio al momento de comprar.
data class DetallePedido(
    val artesaniaId: Int,
    val nombreArtesania: String,
    val cantidad: Int,
    val precioUnitario: Int
) {
    val subtotal: Int
        get() = cantidad * precioUnitario
}
