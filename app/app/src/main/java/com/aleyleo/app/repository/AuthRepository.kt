package com.aleyleo.app.repository

import com.aleyleo.app.datos.DatosPrueba
import com.aleyleo.app.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Repositorio de autenticación. En la E2 valida contra DatosPrueba;
// en la entrega final llamará a POST /api/auth/login y guardará el token JWT.
object AuthRepository {

    private var usuarios = DatosPrueba.usuarios
    private val contrasenas = DatosPrueba.contrasenas

    // Usuario con sesión iniciada (null = nadie conectado).
    private val _sesion = MutableStateFlow<Usuario?>(null)
    val sesion: StateFlow<Usuario?> = _sesion.asStateFlow()

    // Devuelve el usuario si email y contraseña son correctos; si no, null.
    fun iniciarSesion(email: String, password: String): Usuario? {
        val usuario = usuarios.find { it.email.equals(email.trim(), ignoreCase = true) }

        if (usuario != null && contrasenas[usuario.email] == password) {
            _sesion.value = usuario
            return usuario
        }
        return null
    }

    // PUT /api/usuarios/me: guarda los datos editables del perfil (email y RUT no cambian).
    fun actualizarPerfil(nombreCompleto: String, telefono: String, direccion: String, comuna: String): Usuario? {
        val actual = _sesion.value ?: return null

        val actualizado = actual.copy(
            nombreCompleto = nombreCompleto.trim(),
            telefono = telefono.replace(" ", ""),
            direccion = direccion.trim(),
            comuna = comuna.trim()
        )
        usuarios = usuarios.map { if (it.id == actual.id) actualizado else it }
        _sesion.value = actualizado
        return actualizado
    }

    fun cerrarSesion() {
        _sesion.value = null
    }
}
