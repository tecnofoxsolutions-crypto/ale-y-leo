package com.aleyleo.api.usuario.dto

import com.aleyleo.api.common.TelefonoChileno
import com.aleyleo.api.usuario.Rol
import com.aleyleo.api.usuario.Usuario
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

// GET y PUT /api/usuarios/me (HU-03). Nunca incluye la contraseña.
data class UsuarioResponse(
    val id: Long,
    val rut: String,
    val nombreCompleto: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val comuna: String,
    val rol: Rol,
    val fotoUrl: String?
)

// Los controllers devuelven DTO, nunca la entidad JPA (regla de la sección 4.1).
fun Usuario.aResponse() = UsuarioResponse(
    id = id!!,
    rut = rut,
    nombreCompleto = nombreCompleto,
    email = email,
    telefono = telefono,
    direccion = direccion,
    comuna = comuna,
    rol = rol,
    fotoUrl = fotoUrl
)

// PUT /api/usuarios/me: solo estos 4 campos. El email y el RUT no se editan (no están aquí).
data class ActualizarPerfilRequest(
    @field:NotBlank(message = "El nombre es obligatorio")
    @field:Size(max = 100, message = "El nombre tiene máximo 100 caracteres")
    val nombreCompleto: String?,

    @field:NotBlank(message = "El teléfono es obligatorio")
    @field:TelefonoChileno
    val telefono: String?,

    @field:NotBlank(message = "La dirección es obligatoria")
    @field:Size(max = 200, message = "La dirección tiene máximo 200 caracteres")
    val direccion: String?,

    @field:NotBlank(message = "La comuna es obligatoria")
    @field:Size(max = 80, message = "La comuna tiene máximo 80 caracteres")
    val comuna: String?
)
