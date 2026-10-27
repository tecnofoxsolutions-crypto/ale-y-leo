package com.aleyleo.api.usuario

import org.springframework.data.jpa.repository.JpaRepository

// Spring Data crea la consulta a partir del nombre del método (findBy..., existsBy...).
interface UsuarioRepository : JpaRepository<Usuario, Long> {

    // Login: el email es el usuario (sin distinguir mayúsculas).
    fun findByEmailIgnoreCase(email: String): Usuario?

    // Registro: el email y el RUT no se pueden repetir (409).
    fun existsByEmailIgnoreCase(email: String): Boolean

    fun existsByRut(rut: String): Boolean
}
