package com.aleyleo.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

// Punto de entrada del backend. Paquetes (sección 4.1): config, common, auth, usuario,
// categoria, artesania, pedido y archivo, cada uno con Controller, Service y Repository.
@SpringBootApplication
class AleYLeoApplication

fun main(args: Array<String>) {
    runApplication<AleYLeoApplication>(*args)
}
