package com.aleyleo.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla de Room: cada fila es un producto dentro del carro.
// Así el carro se conserva aunque se cierre la app.
@Entity(tableName = "item_carro")
data class ItemCarro(
    @PrimaryKey
    val artesaniaId: Int,
    val nombre: String,
    val precioUnitario: Int,
    val cantidad: Int,
    val stockConocido: Int // tope para el botón "+"
) {
    val subtotal: Int
        get() = cantidad * precioUnitario
}
