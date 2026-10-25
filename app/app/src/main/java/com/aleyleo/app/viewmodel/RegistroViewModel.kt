package com.aleyleo.app.viewmodel

import androidx.lifecycle.ViewModel
import com.aleyleo.app.model.DatosRegistro
import com.aleyleo.app.model.ResultadoAuth
import com.aleyleo.app.repository.AuthRepository
import com.aleyleo.app.util.validarComuna
import com.aleyleo.app.util.validarDireccion
import com.aleyleo.app.util.validarEmail
import com.aleyleo.app.util.validarNombre
import com.aleyleo.app.util.validarPassword
import com.aleyleo.app.util.validarRut
import com.aleyleo.app.util.validarTelefono
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Errores de cada campo del registro (null = sin error).
data class ErroresRegistro(
    val rut: String? = null,
    val nombre: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val direccion: String? = null,
    val comuna: String? = null,
    val password: String? = null,
    val confirmacion: String? = null
) {
    val hayErrores: Boolean
        get() = listOf(rut, nombre, email, telefono, direccion, comuna, password, confirmacion).any { it != null }
}

// Estado del formulario de Registro (P03).
data class FormularioRegistro(
    val rut: String = "",
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val comuna: String = "",
    val password: String = "",
    val confirmacion: String = "",
    val errores: ErroresRegistro = ErroresRegistro(),
    val errorGeneral: String? = null // por ejemplo, email o RUT repetido
)

class RegistroViewModel : ViewModel() {

    private val _formulario = MutableStateFlow(FormularioRegistro())
    val formulario: StateFlow<FormularioRegistro> = _formulario.asStateFlow()

    // Cada cambio borra el error de ese campo, para no dejar un mensaje viejo.
    fun cambiarRut(t: String) = _formulario.update { it.copy(rut = t, errores = it.errores.copy(rut = null)) }
    fun cambiarNombre(t: String) = _formulario.update { it.copy(nombre = t, errores = it.errores.copy(nombre = null)) }
    fun cambiarEmail(t: String) = _formulario.update { it.copy(email = t, errores = it.errores.copy(email = null)) }
    fun cambiarTelefono(t: String) = _formulario.update { it.copy(telefono = t, errores = it.errores.copy(telefono = null)) }
    fun cambiarDireccion(t: String) = _formulario.update { it.copy(direccion = t, errores = it.errores.copy(direccion = null)) }
    fun cambiarComuna(t: String) = _formulario.update { it.copy(comuna = t, errores = it.errores.copy(comuna = null)) }
    fun cambiarPassword(t: String) = _formulario.update { it.copy(password = t, errores = it.errores.copy(password = null)) }
    fun cambiarConfirmacion(t: String) = _formulario.update { it.copy(confirmacion = t, errores = it.errores.copy(confirmacion = null)) }

    // Valida todo, crea la cuenta y deja la sesión iniciada. Devuelve true si salió bien.
    fun registrar(): Boolean {
        val f = _formulario.value

        val errores = validarRegistro(f)
        if (errores.hayErrores) {
            _formulario.update { it.copy(errores = errores, errorGeneral = null) }
            return false
        }

        val resultado = AuthRepository.registrar(
            DatosRegistro(f.rut, f.nombre, f.email, f.telefono, f.direccion, f.comuna, f.password)
        )
        return when (resultado) {
            is ResultadoAuth.Exito -> true
            is ResultadoAuth.Error -> {
                _formulario.update { it.copy(errorGeneral = resultado.mensaje) }
                false
            }
        }
    }
}

// Reglas de HU-01: todo obligatorio, RUT con dígito verificador, email con formato,
// contraseña de 8 o más caracteres e igual a la confirmación.
fun validarRegistro(f: FormularioRegistro): ErroresRegistro {
    return ErroresRegistro(
        rut = validarRut(f.rut),
        nombre = validarNombre(f.nombre),
        email = validarEmail(f.email),
        telefono = validarTelefono(f.telefono),
        direccion = validarDireccion(f.direccion),
        comuna = validarComuna(f.comuna),
        password = validarPassword(f.password),
        confirmacion = when {
            f.confirmacion.isBlank() -> "Repite la contraseña"
            f.confirmacion != f.password -> "Las contraseñas no coinciden"
            else -> null
        }
    )
}
