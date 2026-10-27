package com.aleyleo.api.usuario

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

// Tabla "usuario" (sección 5.1). El email es el usuario de login.
// No es data class: en JPA, equals/hashCode generados con todos los campos dan problemas.
@Entity
@Table(name = "usuario")
class Usuario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true, length = 12)
    var rut: String = "",

    @Column(name = "nombre_completo", nullable = false, length = 100)
    var nombreCompleto: String = "",

    @Column(nullable = false, unique = true, length = 120)
    var email: String = "",

    @Column(nullable = false, length = 20)
    var telefono: String = "",

    @Column(nullable = false, length = 200)
    var direccion: String = "",

    @Column(nullable = false, length = 80)
    var comuna: String = "",

    // Contraseña cifrada con BCrypt. Nunca se devuelve en la API.
    @Column(name = "password_hash", nullable = false, length = 100)
    var passwordHash: String = "",

    @Enumerated(EnumType.STRING)      // guarda "CLIENTE" o "ADMIN", no un número
    @JdbcTypeCode(SqlTypes.VARCHAR)   // columna VARCHAR (no ENUM de MySQL), como en la tabla 5.1
    @Column(nullable = false, length = 10)
    var rol: Rol = Rol.CLIENTE,

    @Column(name = "foto_url", length = 255)
    var fotoUrl: String? = null,

    @Column(nullable = false)
    var activo: Boolean = true,

    @Column(name = "fecha_creacion", nullable = false)
    var fechaCreacion: LocalDateTime = LocalDateTime.now()
)
