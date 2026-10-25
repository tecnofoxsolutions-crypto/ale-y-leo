package com.aleyleo.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.aleyleo.app.database.AppDatabase
import com.aleyleo.app.model.Usuario
import com.aleyleo.app.repository.AuthRepository
import com.aleyleo.app.repository.CarroRepository
import com.aleyleo.app.repository.FavoritoRepository
import com.aleyleo.app.util.validarComuna
import com.aleyleo.app.util.validarDireccion
import com.aleyleo.app.util.validarNombre
import com.aleyleo.app.util.validarTelefono
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Formulario de Editar perfil (P14). Cada error es null si el campo está bien.
data class FormularioPerfil(
    val nombreCompleto: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val comuna: String = "",
    val errorNombre: String? = null,
    val errorTelefono: String? = null,
    val errorDireccion: String? = null,
    val errorComuna: String? = null
)

// ViewModel de Perfil (P13) y Editar perfil (P14). Lo usan el cliente y la admin.
// AndroidViewModel porque al cerrar sesión vacía el carro y los favoritos de Room.
class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val carroRepository: CarroRepository
    private val favoritoRepository: FavoritoRepository

    val sesion: StateFlow<Usuario?> = AuthRepository.sesion

    private val _formulario = MutableStateFlow(FormularioPerfil())
    val formulario: StateFlow<FormularioPerfil> = _formulario.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        carroRepository = CarroRepository(database.itemCarroDao())
        favoritoRepository = FavoritoRepository(database.favoritoDao())

        // El formulario parte con los datos actuales del usuario.
        sesion.value?.let { usuario ->
            _formulario.value = FormularioPerfil(
                nombreCompleto = usuario.nombreCompleto,
                telefono = usuario.telefono,
                direccion = usuario.direccion,
                comuna = usuario.comuna
            )
        }
    }

    fun cambiarNombre(texto: String) = _formulario.update { it.copy(nombreCompleto = texto, errorNombre = null) }
    fun cambiarTelefono(texto: String) = _formulario.update { it.copy(telefono = texto, errorTelefono = null) }
    fun cambiarDireccion(texto: String) = _formulario.update { it.copy(direccion = texto, errorDireccion = null) }
    fun cambiarComuna(texto: String) = _formulario.update { it.copy(comuna = texto, errorComuna = null) }

    // Valida con las mismas reglas del registro (HU-03). Devuelve true si se guardó.
    fun guardar(): Boolean {
        val f = _formulario.value
        val conErrores = f.copy(
            errorNombre = validarNombre(f.nombreCompleto),
            errorTelefono = validarTelefono(f.telefono),
            errorDireccion = validarDireccion(f.direccion),
            errorComuna = validarComuna(f.comuna)
        )
        _formulario.value = conErrores

        val hayErrores = listOf(
            conErrores.errorNombre, conErrores.errorTelefono,
            conErrores.errorDireccion, conErrores.errorComuna
        ).any { it != null }
        if (hayErrores) return false

        return AuthRepository.actualizarPerfil(f.nombreCompleto, f.telefono, f.direccion, f.comuna) != null
    }

    // Cierra sesión (HU-03): borra la sesión y vacía carro y favoritos (D11).
    // "suspend" para que la pantalla espere a que Room termine antes de ir al Login.
    suspend fun cerrarSesion() {
        carroRepository.carroVaciar()
        favoritoRepository.favoritosVaciar()
        AuthRepository.cerrarSesion() // en la entrega final, aquí se borra también el token JWT
    }
}
