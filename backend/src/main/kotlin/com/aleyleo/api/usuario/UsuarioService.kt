package com.aleyleo.api.usuario

import com.aleyleo.api.common.NoEncontradoException
import com.aleyleo.api.common.normalizarTelefono
import com.aleyleo.api.usuario.dto.ActualizarPerfilRequest
import com.aleyleo.api.usuario.dto.UsuarioResponse
import com.aleyleo.api.usuario.dto.aResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

// Perfil del usuario logueado (HU-03).
@Service
class UsuarioService(private val usuarioRepository: UsuarioRepository) {

    @Transactional(readOnly = true)
    fun obtenerPerfil(id: Long): UsuarioResponse = buscar(id).aResponse()

    // Solo cambia nombre, teléfono, dirección y comuna. Email y RUT quedan igual.
    @Transactional
    fun actualizarPerfil(id: Long, datos: ActualizarPerfilRequest): UsuarioResponse {
        val usuario = buscar(id)
        usuario.nombreCompleto = datos.nombreCompleto!!.trim()
        usuario.telefono = normalizarTelefono(datos.telefono!!)
        usuario.direccion = datos.direccion!!.trim()
        usuario.comuna = datos.comuna!!.trim()
        // Dentro de @Transactional, JPA guarda los cambios solo al terminar el método.
        return usuario.aResponse()
    }

    private fun buscar(id: Long): Usuario =
        usuarioRepository.findById(id).orElseThrow { NoEncontradoException("Usuario no encontrado") }
}
