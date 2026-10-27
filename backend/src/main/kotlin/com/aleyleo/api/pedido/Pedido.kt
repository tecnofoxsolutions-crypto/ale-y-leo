package com.aleyleo.api.pedido

import com.aleyleo.api.usuario.Usuario
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

// Tabla "pedido". Guarda una copia de la dirección por si el cliente la cambia después.
// El total lo calcula el servidor con el precio vigente; la app no lo envía.
@Entity
@Table(name = "pedido")
class Pedido(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    // Formato PED-2026-0001.
    @Column(nullable = false, unique = true, length = 20)
    var codigo: String = "",

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    var usuario: Usuario,

    @Enumerated(EnumType.STRING)      // guarda el nombre del estado, por ejemplo "PAGADA"
    @JdbcTypeCode(SqlTypes.VARCHAR)   // columna VARCHAR (no ENUM de MySQL), como en la tabla 5.1
    @Column(nullable = false, length = 20)
    var estado: EstadoPedido = EstadoPedido.GENERADA,

    @Column(nullable = false)
    var total: Int = 0,

    @Column(name = "direccion_despacho", nullable = false, length = 200)
    var direccionDespacho: String = "",

    @Column(name = "comuna_despacho", nullable = false, length = 80)
    var comunaDespacho: String = "",

    @Column(length = 500)
    var notas: String? = null,

    @Column(name = "fecha_creacion", nullable = false)
    var fechaCreacion: LocalDateTime = LocalDateTime.now(),

    @Column(name = "fecha_pago")
    var fechaPago: LocalDateTime? = null,

    @Column(name = "fecha_actualizacion", nullable = false)
    var fechaActualizacion: LocalDateTime = LocalDateTime.now(),

    // Un pedido tiene muchos detalles. cascade = ALL: al guardar el pedido se guardan sus detalles.
    @OneToMany(mappedBy = "pedido", cascade = [CascadeType.ALL], orphanRemoval = true)
    var detalles: MutableList<DetallePedido> = mutableListOf()
) {
    // Agrega una línea y deja enlazados los dos lados de la relación.
    fun agregarDetalle(detalle: DetallePedido) {
        detalle.pedido = this
        detalles.add(detalle)
    }
}
