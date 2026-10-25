package com.aleyleo.app.viewmodel

import androidx.lifecycle.ViewModel
import com.aleyleo.app.model.Rol
import com.aleyleo.app.model.Usuario
import com.aleyleo.app.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Estado del formulario de Login (P02).
data class FormularioLogin(
    val email: String = "",
    val password: String = "",
    val error: String? = null
)

// ViewModel de Splash (P01) y Login (P02).
class AuthViewModel : ViewModel() {

    private val repository = AuthRepository

    val sesion: StateFlow<Usuario?> = repository.sesion

    private val _formulario = MutableStateFlow(FormularioLogin())
    val formulario: StateFlow<FormularioLogin> = _formulario.asStateFlow()

    fun cambiarEmail(texto: String) = _formulario.update { it.copy(email = texto, error = null) }
    fun cambiarPassword(texto: String) = _formulario.update { it.copy(password = texto, error = null) }

    // Devuelve el rol para que la pantalla sepa a qué grafo ir, o null si falló (HU-02).
    fun iniciarSesion(): Rol? {
        val f = _formulario.value

        if (f.email.isBlank() || f.password.isBlank()) {
            _formulario.update { it.copy(error = "Ingresa tu email y tu contraseña") }
            return null
        }

        val usuario = repository.iniciarSesion(f.email, f.password)
        if (usuario == null) {
            // Mensaje genérico: no dice si falló el email o la contraseña (seguridad).
            _formulario.update { it.copy(error = "Email o contraseña incorrectos") }
            return null
        }
        return usuario.rol
    }
}
