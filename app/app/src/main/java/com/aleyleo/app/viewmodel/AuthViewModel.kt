package com.aleyleo.app.viewmodel

import androidx.lifecycle.ViewModel
import com.aleyleo.app.model.Usuario
import com.aleyleo.app.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow

// ViewModel de sesión (Splash, Login y Registro). AP-06 le agrega el formulario y las validaciones.
class AuthViewModel : ViewModel() {

    private val repository = AuthRepository

    val sesion: StateFlow<Usuario?> = repository.sesion

    fun iniciarSesion(email: String, password: String): Usuario? {
        return repository.iniciarSesion(email, password)
    }

    fun cerrarSesion() {
        repository.cerrarSesion()
    }
}
