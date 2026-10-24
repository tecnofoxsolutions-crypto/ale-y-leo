package com.aleyleo.app.model

data class Categoria(
    val id: Int,
    val nombre: String,
    val descripcion: String = "",
    val activo: Boolean = true // borrado lógico: inactiva = oculta para el cliente
)
