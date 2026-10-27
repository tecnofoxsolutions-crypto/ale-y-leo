package com.aleyleo.api.common

import com.fasterxml.jackson.annotation.JsonInclude

// Formato único de los errores de la API (sección 4.1 y 6.3), por ejemplo:
// { "status": 409, "error": "STOCK_INSUFICIENTE", "mensaje": "No hay stock suficiente de 'Paila de Cobre'" }
// "campos" solo aparece en los errores 400 de validación: { "email": "El email no es válido", ... }
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiError(
    val status: Int,
    val error: String,
    val mensaje: String,
    val campos: Map<String, String>? = null
)
