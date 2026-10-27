package com.aleyleo.api.config

import com.aleyleo.api.common.ApiError
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

// BE-07: qué rutas son públicas, cuáles exigen token y cuáles son solo de la admin (sección 6.1).
@Configuration
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter,
    private val objectMapper: ObjectMapper
) {

    // Las contraseñas se guardan cifradas con BCrypt (nunca en texto plano).
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // API REST con token: no hay formularios ni cookies de sesión, así que CSRF no aplica.
            .csrf { it.disable() }
            // Stateless: el servidor no guarda sesiones; cada petición trae su token.
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                // Públicas: registro, login, lectura del catálogo e imágenes.
                it.requestMatchers(HttpMethod.POST, "/api/auth/registro", "/api/auth/login").permitAll()
                it.requestMatchers(HttpMethod.GET, "/api/categorias", "/api/artesanias", "/api/artesanias/**").permitAll()
                it.requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                it.requestMatchers("/error").permitAll()
                // Solo la admin.
                it.requestMatchers("/api/admin/**").hasRole("ADMIN")
                // Comprar y ver pedidos propios es del cliente.
                it.requestMatchers("/api/pedidos/**").hasRole("CLIENTE")
                // Todo lo demás (por ejemplo /api/usuarios/me) exige estar logueado.
                it.anyRequest().authenticated()
            }
            .exceptionHandling {
                // Sin token, token alterado o vencido.
                it.authenticationEntryPoint { _, response, _ ->
                    escribirError(response, 401, "NO_AUTENTICADO", "Debes iniciar sesión")
                }
                // Con token, pero sin el rol necesario.
                it.accessDeniedHandler { _, response, _ ->
                    escribirError(response, 403, "ACCESO_DENEGADO", "No tienes permiso para esta acción")
                }
            }
            // El filtro JWT corre antes que el filtro de login por formulario de Spring.
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    // Los errores de seguridad ocurren antes del controller, por eso se escriben aquí con el mismo ApiError.
    private fun escribirError(response: HttpServletResponse, status: Int, error: String, mensaje: String) {
        response.status = status
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        objectMapper.writeValue(response.writer, ApiError(status, error, mensaje))
    }
}
