package com.aleyleo.app.model

// Un producto del catálogo. Los precios son enteros en pesos chilenos (CLP).
data class Artesania(
    val id: Int,
    val categoriaId: Int,
    val nombre: String,
    val descripcion: String,
    val materialesTecnica: String,
    val precioBase: Int,
    val stock: Int,
    val imagenUrl: String,
    val activo: Boolean = true
) {
    val agotada: Boolean
        get() = stock == 0
}
