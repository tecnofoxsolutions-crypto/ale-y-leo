package com.aleyleo.api.categoria.dto

import com.aleyleo.api.categoria.Categoria
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CategoriaResponse(
    val id: Long,
    val nombre: String,
    val descripcion: String?,
    val activo: Boolean
)

fun Categoria.aResponse() = CategoriaResponse(id!!, nombre, descripcion, activo)

// POST y PUT /api/admin/categorias (HU-19). Nombre obligatorio y único; descripción opcional.
// "activo" es opcional: sirve para volver a activar una categoría desde la app.
data class CategoriaRequest(
    @field:NotBlank(message = "El nombre es obligatorio")
    @field:Size(max = 50, message = "El nombre tiene máximo 50 caracteres")
    val nombre: String?,

    @field:Size(max = 200, message = "La descripción tiene máximo 200 caracteres")
    val descripcion: String? = null,

    val activo: Boolean? = null
)
