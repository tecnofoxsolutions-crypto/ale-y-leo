package com.aleyleo.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla de Room: favoritos guardados en el teléfono.
// La clave es el id de la artesanía, así una pieza no puede quedar dos veces.
@Entity(tableName = "favorito")
data class Favorito(
    @PrimaryKey
    val artesaniaId: Int,
    val nombre: String,
    val precio: Int,
    val fechaGuardado: Long = System.currentTimeMillis()
)
