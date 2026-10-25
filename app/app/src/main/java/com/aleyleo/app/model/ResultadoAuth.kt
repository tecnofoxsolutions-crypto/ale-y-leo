package com.aleyleo.app.model

// Respuesta del registro: salió bien (con el usuario creado) o falló (con el motivo).
sealed class ResultadoAuth {
    data class Exito(val usuario: Usuario) : ResultadoAuth()
    data class Error(val mensaje: String) : ResultadoAuth()
}

// Datos que envía el formulario de Registro (igual que el body de POST /api/auth/registro).
data class DatosRegistro(
    val rut: String,
    val nombreCompleto: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val comuna: String,
    val password: String
)
