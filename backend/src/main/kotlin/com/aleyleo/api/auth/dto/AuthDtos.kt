package com.aleyleo.api.auth.dto

import com.aleyleo.api.common.RutValido
import com.aleyleo.api.common.TelefonoChileno
import com.aleyleo.api.usuario.Rol
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

// POST /api/auth/registro (HU-01). Todos los campos son obligatorios.
// Los campos son "String?" para que un campo que falta llegue como null y lo reporte @NotBlank (400),
// en vez de que el JSON falle completo.
data class RegistroRequest(
    @field:NotBlank(message = "El RUT es obligatorio")
    @field:RutValido
    val rut: String?,

    @field:NotBlank(message = "El nombre es obligatorio")
    @field:Size(max = 100, message = "El nombre tiene máximo 100 caracteres")
    val nombreCompleto: String?,

    @field:NotBlank(message = "El email es obligatorio")
    @field:Email(message = "El email no es válido")
    @field:Size(max = 120, message = "El email tiene máximo 120 caracteres")
    val email: String?,

    @field:NotBlank(message = "El teléfono es obligatorio")
    @field:TelefonoChileno
    val telefono: String?,

    @field:NotBlank(message = "La dirección es obligatoria")
    @field:Size(max = 200, message = "La dirección tiene máximo 200 caracteres")
    val direccion: String?,

    @field:NotBlank(message = "La comuna es obligatoria")
    @field:Size(max = 80, message = "La comuna tiene máximo 80 caracteres")
    val comuna: String?,

    @field:NotBlank(message = "La contraseña es obligatoria")
    @field:Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    val password: String?
)

// POST /api/auth/login (HU-02).
data class LoginRequest(
    @field:NotBlank(message = "El email es obligatorio")
    val email: String?,

    @field:NotBlank(message = "La contraseña es obligatoria")
    val password: String?
)

// Respuesta de registro y login (sección 6.3).
data class AuthResponse(
    val token: String,
    val tipo: String = "Bearer",
    val expiraEn: Long,
    val usuario: UsuarioResumen
)

data class UsuarioResumen(
    val id: Long,
    val nombreCompleto: String,
    val email: String,
    val rol: Rol
)
