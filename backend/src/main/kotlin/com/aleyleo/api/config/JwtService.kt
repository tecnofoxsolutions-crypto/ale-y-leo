package com.aleyleo.api.config

import com.aleyleo.api.usuario.Rol
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

// Quién hizo la petición, según su token. Es lo que los controllers reciben como usuario logueado.
data class UsuarioAutenticado(val id: Long, val email: String, val rol: Rol)

// BE-08: crea y valida los tokens JWT.
// El token va firmado con una clave secreta: si alguien cambia una letra, la firma ya no calza.
@Service
class JwtService(
    @Value("\${jwt.secreto}") secreto: String,
    @Value("\${jwt.expiracion-segundos}") val expiracionSegundos: Long
) {
    // HMAC-SHA256 necesita una clave de al menos 32 bytes.
    private val clave: SecretKey = Keys.hmacShaKeyFor(secreto.toByteArray())

    // Token con el email como "subject" y el id y el rol como datos extra (claims).
    fun generarToken(id: Long, email: String, rol: Rol, segundos: Long = expiracionSegundos): String {
        val ahora = Date()
        return Jwts.builder()
            .subject(email)
            .claim("id", id)
            .claim("rol", rol.name)
            .issuedAt(ahora)
            .expiration(Date(ahora.time + segundos * 1000))
            .signWith(clave)
            .compact()
    }

    // Devuelve el usuario del token, o null si el token está alterado, vencido o mal formado.
    fun leerToken(token: String): UsuarioAutenticado? {
        return try {
            val claims = Jwts.parser()
                .verifyWith(clave) // revisa la firma
                .build()
                .parseSignedClaims(token) // también revisa que no esté vencido
                .payload
            UsuarioAutenticado(
                id = (claims["id"] as Number).toLong(),
                email = claims.subject,
                rol = Rol.valueOf(claims["rol"] as String)
            )
        } catch (e: JwtException) {
            null
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}
