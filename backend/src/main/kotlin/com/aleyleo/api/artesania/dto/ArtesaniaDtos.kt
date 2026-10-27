package com.aleyleo.api.artesania.dto

import com.aleyleo.api.artesania.Artesania
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size

// ArtesaniaResponse de la sección 6.3. La categoría va resumida (id y nombre).
data class ArtesaniaResponse(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val materialesTecnica: String,
    val precioBase: Int,
    val stock: Int,
    val imagenUrl: String?,
    val activo: Boolean,
    val categoria: CategoriaResumen
)

data class CategoriaResumen(val id: Long, val nombre: String)

fun Artesania.aResponse() = ArtesaniaResponse(
    id = id!!,
    nombre = nombre,
    descripcion = descripcion,
    materialesTecnica = materialesTecnica,
    precioBase = precioBase,
    stock = stock,
    imagenUrl = imagenUrl,
    activo = activo,
    categoria = CategoriaResumen(categoria.id!!, categoria.nombre)
)

// POST y PUT /api/admin/artesanias (HU-16): nombre, categoría, descripción y materiales
// obligatorios; precio > 0; stock >= 0. La imagen se sube aparte (endpoint 22, BE-12).
// "activo" es opcional: sirve para volver a activar una artesanía desactivada.
data class ArtesaniaRequest(
    @field:NotNull(message = "Elige una categoría")
    val categoriaId: Long?,

    @field:NotBlank(message = "El nombre es obligatorio")
    @field:Size(max = 100, message = "El nombre tiene máximo 100 caracteres")
    val nombre: String?,

    @field:NotBlank(message = "La descripción es obligatoria")
    @field:Size(max = 500, message = "La descripción tiene máximo 500 caracteres")
    val descripcion: String?,

    @field:NotBlank(message = "Indica los materiales o la técnica")
    @field:Size(max = 200, message = "Materiales o técnica tiene máximo 200 caracteres")
    val materialesTecnica: String?,

    @field:NotNull(message = "El precio es obligatorio")
    @field:Positive(message = "El precio debe ser mayor que 0")
    val precioBase: Int?,

    @field:NotNull(message = "El stock es obligatorio")
    @field:PositiveOrZero(message = "El stock no puede ser negativo")
    val stock: Int?,

    val activo: Boolean? = null
)
