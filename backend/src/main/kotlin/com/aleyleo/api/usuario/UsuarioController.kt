package com.aleyleo.api.usuario

import com.aleyleo.api.config.UsuarioAutenticado
import com.aleyleo.api.usuario.dto.ActualizarPerfilRequest
import com.aleyleo.api.usuario.dto.UsuarioResponse
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// Endpoints 3 y 4 (requieren token). "me" = el usuario del token: nadie puede ver ni editar a otro.
@RestController
@RequestMapping("/api/usuarios/me")
class UsuarioController(private val usuarioService: UsuarioService) {

    @GetMapping
    fun obtener(@AuthenticationPrincipal usuario: UsuarioAutenticado): UsuarioResponse =
        usuarioService.obtenerPerfil(usuario.id)

    @PutMapping
    fun actualizar(
        @AuthenticationPrincipal usuario: UsuarioAutenticado,
        @Valid @RequestBody datos: ActualizarPerfilRequest
    ): UsuarioResponse = usuarioService.actualizarPerfil(usuario.id, datos)
}
