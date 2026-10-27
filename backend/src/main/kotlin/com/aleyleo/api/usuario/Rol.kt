package com.aleyleo.api.usuario

// Rol del usuario: define qué rutas de la API puede usar (las /api/admin/** son solo ADMIN).
enum class Rol {
    CLIENTE,
    ADMIN
}
