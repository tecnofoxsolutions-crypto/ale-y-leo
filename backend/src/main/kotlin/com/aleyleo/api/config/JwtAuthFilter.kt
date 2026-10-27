package com.aleyleo.api.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

// BE-08: se ejecuta una vez en cada petición, antes de llegar al controller.
// Si trae "Authorization: Bearer <token>" válido, deja registrado quién es y con qué rol.
// Si no trae token o es inválido, no hace nada: SecurityConfig decide si la ruta era pública o responde 401.
@Component
class JwtAuthFilter(private val jwtService: JwtService) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val cabecera = request.getHeader("Authorization")

        if (cabecera != null && cabecera.startsWith("Bearer ")) {
            val usuario = jwtService.leerToken(cabecera.removePrefix("Bearer ").trim())
            if (usuario != null) {
                // "ROLE_ADMIN" o "ROLE_CLIENTE": así funciona hasRole("ADMIN") en SecurityConfig.
                val permisos = listOf(SimpleGrantedAuthority("ROLE_${usuario.rol.name}"))
                SecurityContextHolder.getContext().authentication =
                    UsernamePasswordAuthenticationToken(usuario, null, permisos)
            }
        }

        filterChain.doFilter(request, response)
    }
}
