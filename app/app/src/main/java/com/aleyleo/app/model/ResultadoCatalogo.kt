package com.aleyleo.app.model

// Resultado de crear, editar o desactivar algo del catálogo (admin).
// En la entrega final, Error corresponde a un 400 o 409 del backend.
sealed class ResultadoCatalogo {
    data class Exito(val id: Int) : ResultadoCatalogo()
    data class Error(val mensaje: String) : ResultadoCatalogo()
}
