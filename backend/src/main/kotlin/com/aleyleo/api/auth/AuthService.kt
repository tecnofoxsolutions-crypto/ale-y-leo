package com.aleyleo.api.auth

import com.aleyleo.api.auth.dto.AuthResponse
import com.aleyleo.api.auth.dto.LoginRequest
import com.aleyleo.api.auth.dto.RegistroRequest
import com.aleyleo.api.auth.dto.UsuarioResumen
import com.aleyleo.api.common.ConflictoException
import com.aleyleo.api.common.CredencialesInvalidasException
import com.aleyleo.api.common.formatearRut
import com.aleyleo.api.common.normalizarTelefono
import com.aleyleo.api.config.JwtService
import com.aleyleo.api.usuario.Rol
import com.aleyleo.api.usuario.Usuario
import com.aleyleo.api.usuario.UsuarioRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

// Registro (HU-01) e inicio de sesión (HU-02).
@Service
class AuthService(
    private val usuarioRepository: UsuarioRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {

    // Crea siempre un CLIENTE (la admin no se registra desde la app) y devuelve su token,
    // así queda con la sesión iniciada.
    @Transactional
    fun registrar(datos: RegistroRequest): AuthResponse {
        val email = datos.email!!.trim().lowercase()
        val rut = formatearRut(datos.rut!!)

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw ConflictoException("EMAIL_REGISTRADO", "Ya existe una cuenta con ese email")
        }
        if (usuarioRepository.existsByRut(rut)) {
            throw ConflictoException("RUT_REGISTRADO", "Ya existe una cuenta con ese RUT")
        }

        val usuario = usuarioRepository.save(
            Usuario(
                rut = rut,
                nombreCompleto = datos.nombreCompleto!!.trim(),
                email = email,
                telefono = normalizarTelefono(datos.telefono!!),
                direccion = datos.direccion!!.trim(),
                comuna = datos.comuna!!.trim(),
                passwordHash = passwordEncoder.encode(datos.password), // nunca en texto plano
                rol = Rol.CLIENTE
            )
        )
        return respuestaCon(usuario)
    }

    // Con un mensaje único, sin decir si falló el email o la contraseña (HU-02).
    @Transactional(readOnly = true)
    fun iniciarSesion(datos: LoginRequest): AuthResponse {
        val usuario = usuarioRepository.findByEmailIgnoreCase(datos.email!!.trim())
        val correcta = usuario != null && usuario.activo && passwordEncoder.matches(datos.password, usuario.passwordHash)
        if (!correcta) throw CredencialesInvalidasException()

        return respuestaCon(usuario!!)
    }

    private fun respuestaCon(usuario: Usuario) = AuthResponse(
        token = jwtService.generarToken(usuario.id!!, usuario.email, usuario.rol),
        expiraEn = jwtService.expiracionSegundos,
        usuario = UsuarioResumen(usuario.id!!, usuario.nombreCompleto, usuario.email, usuario.rol)
    )
}
