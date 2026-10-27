package com.aleyleo.api.pedido

import com.aleyleo.api.artesania.Artesania
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

// Tabla "detalle_pedido": una línea del pedido. Copia el nombre y el precio al comprar,
// así el pedido no cambia si después se edita la artesanía.
@Entity
@Table(name = "detalle_pedido")
class DetallePedido(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    var pedido: Pedido? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artesania_id", nullable = false)
    var artesania: Artesania,

    @Column(name = "nombre_artesania", nullable = false, length = 100)
    var nombreArtesania: String = "",

    @Column(nullable = false)
    var cantidad: Int = 1,

    @Column(name = "precio_unitario", nullable = false)
    var precioUnitario: Int = 0,

    @Column(nullable = false)
    var subtotal: Int = 0
)
