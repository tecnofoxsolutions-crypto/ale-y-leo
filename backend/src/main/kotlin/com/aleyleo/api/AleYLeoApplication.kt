package com.aleyleo.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication

// Punto de entrada del backend. Paquetes (sección 4.1): config, common, auth, usuario,
// categoria, artesania, pedido y archivo, cada uno con Controller, Service y Repository.
// Se excluye el usuario "user" con contraseña aleatoria que Spring crea por defecto:
// aquí el login es con JWT (BE-07 y BE-08).
@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
class AleYLeoApplication

fun main(args: Array<String>) {
    runApplication<AleYLeoApplication>(*args)
}
