package com.aleyleo.api.auth

import com.aleyleo.api.auth.dto.AuthResponse
import com.aleyleo.api.auth.dto.LoginRequest
import com.aleyleo.api.auth.dto.RegistroRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

// Endpoints 1 y 2 (públicos). @Valid revisa las anotaciones del DTO; si algo falla, responde 400.
@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED) // 201
    fun registrar(@Valid @RequestBody datos: RegistroRequest): AuthResponse = authService.registrar(datos)

    @PostMapping("/login")
    fun iniciarSesion(@Valid @RequestBody datos: LoginRequest): AuthResponse = authService.iniciarSesion(datos)
}
