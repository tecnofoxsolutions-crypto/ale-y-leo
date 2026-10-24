package com.aleyleo.app.model

// Datos del usuario que la app muestra. La contraseña NO va aquí:
// la guarda el repositorio (y en la entrega final, solo el backend).
data class Usuario(
    val id: Int,
    val rut: String,
    val nombreCompleto: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val comuna: String,
    val rol: Rol
)
