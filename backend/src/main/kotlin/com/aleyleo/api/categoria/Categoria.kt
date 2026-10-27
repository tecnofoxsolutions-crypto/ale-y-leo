package com.aleyleo.api.categoria

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

// Tabla "categoria". "activo" es el borrado lógico: inactiva = oculta para el cliente.
@Entity
@Table(name = "categoria")
class Categoria(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true, length = 50)
    var nombre: String = "",

    @Column(length = 200)
    var descripcion: String? = null,

    @Column(nullable = false)
    var activo: Boolean = true
)
