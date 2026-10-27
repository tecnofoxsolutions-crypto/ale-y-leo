package com.aleyleo.api.artesania

import com.aleyleo.api.categoria.Categoria
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

// Tabla "artesania". Muchas artesanías pertenecen a una categoría (FK categoria_id).
// Precios enteros en pesos chilenos (CLP).
@Entity
@Table(name = "artesania")
class Artesania(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    var categoria: Categoria,

    @Column(nullable = false, length = 100)
    var nombre: String = "",

    @Column(nullable = false, length = 500)
    var descripcion: String = "",

    @Column(name = "materiales_tecnica", nullable = false, length = 200)
    var materialesTecnica: String = "",

    @Column(name = "precio_base", nullable = false)
    var precioBase: Int = 0,

    @Column(nullable = false)
    var stock: Int = 0,

    // Ruta servida por el backend, por ejemplo "/uploads/art-1.jpg".
    @Column(name = "imagen_url", length = 255)
    var imagenUrl: String? = null,

    @Column(nullable = false)
    var activo: Boolean = true,

    @Column(name = "fecha_creacion", nullable = false)
    var fechaCreacion: LocalDateTime = LocalDateTime.now()
)
