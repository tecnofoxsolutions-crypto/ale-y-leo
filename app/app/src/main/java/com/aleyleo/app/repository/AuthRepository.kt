package com.aleyleo.app.repository

import com.aleyleo.app.datos.DatosPrueba
import com.aleyleo.app.model.DatosRegistro
import com.aleyleo.app.model.ResultadoAuth
import com.aleyleo.app.model.Rol
import com.aleyleo.app.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Repositorio de autenticación. En la E2 valida contra DatosPrueba (en memoria);
// en la entrega final llamará a POST /api/auth/login y /registro y guardará el token JWT.
object AuthRepository {

    private var usuarios = DatosPrueba.usuarios
    private var contrasenas = DatosPrueba.contrasenas // email -> contraseña (solo de desarrollo)

    // Usuario con sesión iniciada (null = nadie conectado).
    private val _sesion = MutableStateFlow<Usuario?>(null)
    val sesion: StateFlow<Usuario?> = _sesion.asStateFlow()

    // POST /api/auth/login (HU-02): devuelve el usuario si email y contraseña son correctos; si no, null.
    fun iniciarSesion(email: String, password: String): Usuario? {
        val usuario = usuarios.find { it.email.equals(email.trim(), ignoreCase = true) }

        if (usuario != null && contrasenas[usuario.email] == password) {
            _sesion.value = usuario
            return usuario
        }
        return null
    }

    // POST /api/auth/registro (HU-01): crea un CLIENTE y deja la sesión iniciada.
    // Si el email o el RUT ya existen, responde el error (en el backend es un 409).
    fun registrar(datos: DatosRegistro): ResultadoAuth {
        val email = datos.email.trim().lowercase()
        val rut = normalizarRut(datos.rut)

        if (usuarios.any { it.email.equals(email, ignoreCase = true) }) {
            return ResultadoAuth.Error("Ya existe una cuenta con ese email")
        }
        if (usuarios.any { normalizarRut(it.rut) == rut }) {
            return ResultadoAuth.Error("Ya existe una cuenta con ese RUT")
        }

        val nuevo = Usuario(
            id = (usuarios.maxOfOrNull { it.id } ?: 0) + 1,
            rut = datos.rut.trim().uppercase(),
            nombreCompleto = datos.nombreCompleto.trim(),
            email = email,
            telefono = datos.telefono.replace(" ", ""),
            direccion = datos.direccion.trim(),
            comuna = datos.comuna.trim(),
            rol = Rol.CLIENTE // el registro solo crea clientes; la admin ya existe
        )
        usuarios = usuarios + nuevo
        contrasenas = contrasenas + (email to datos.password) // en el backend se guarda con BCrypt
        _sesion.value = nuevo
        return ResultadoAuth.Exito(nuevo)
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

    // Datos del cliente de un pedido (los ve la admin en el detalle, HU-14).
    fun obtenerUsuario(id: Int): Usuario? {
        return usuarios.find { it.id == id }
    }

    fun cerrarSesion() {
        _sesion.value = null
    }

    // Vuelve a los datos iniciales (lo usan las pruebas unitarias).
    fun reiniciar() {
        usuarios = DatosPrueba.usuarios
        contrasenas = DatosPrueba.contrasenas
        _sesion.value = null
    }

    // "12.345.678-5" y "123456785" son el mismo RUT.
    private fun normalizarRut(rut: String): String {
        return rut.replace(".", "").replace("-", "").trim().uppercase()
    }
}
