package com.aleyleo.api.artesania

import com.aleyleo.api.artesania.dto.ArtesaniaRequest
import com.aleyleo.api.artesania.dto.ArtesaniaResponse
import com.aleyleo.api.artesania.dto.aResponse
import com.aleyleo.api.categoria.Categoria
import com.aleyleo.api.categoria.CategoriaRepository
import com.aleyleo.api.common.NoEncontradoException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

// Reglas del catálogo (HU-04, HU-05, HU-06) y de su administración (HU-16).
@Service
class ArtesaniaService(
    private val artesaniaRepository: ArtesaniaRepository,
    private val categoriaRepository: CategoriaRepository
) {

    // Endpoint 7 (público): solo activas de categorías activas; filtros opcionales.
    @Transactional(readOnly = true)
    fun buscar(categoriaId: Long?, texto: String?): List<ArtesaniaResponse> =
        artesaniaRepository.buscarActivas(categoriaId, texto?.trim()?.ifEmpty { null }).map { it.aResponse() }

    // Endpoint 8 (público): una inactiva, o de una categoría inactiva, no existe para el cliente (404).
    @Transactional(readOnly = true)
    fun obtenerPublica(id: Long): ArtesaniaResponse {
        val artesania = buscar(id)
        if (!artesania.activo || !artesania.categoria.activo) throw NoEncontradoException("Artesanía no encontrada")
        return artesania.aResponse()
    }

    // Endpoint 18 (admin): todas, también las inactivas.
    @Transactional(readOnly = true)
    fun listarTodas(): List<ArtesaniaResponse> = artesaniaRepository.findAllConCategoria().map { it.aResponse() }

    // Endpoint 19.
    @Transactional
    fun crear(datos: ArtesaniaRequest): ArtesaniaResponse {
        val artesania = Artesania(categoria = buscarCategoria(datos.categoriaId!!))
        copiarDatos(datos, artesania)
        return artesaniaRepository.save(artesania).aResponse()
    }

    // Endpoint 20: edita datos, precio y stock (y puede volver a activarla).
    @Transactional
    fun editar(id: Long, datos: ArtesaniaRequest): ArtesaniaResponse {
        val artesania = buscar(id)
        artesania.categoria = buscarCategoria(datos.categoriaId!!)
        copiarDatos(datos, artesania)
        datos.activo?.let { artesania.activo = it }
        return artesania.aResponse()
    }

    // Endpoint 21: borrado lógico. Deja de verse en el catálogo, pero los pedidos antiguos la siguen nombrando.
    @Transactional
    fun desactivar(id: Long) {
        buscar(id).activo = false
    }

    private fun copiarDatos(datos: ArtesaniaRequest, artesania: Artesania) {
        artesania.nombre = datos.nombre!!.trim()
        artesania.descripcion = datos.descripcion!!.trim()
        artesania.materialesTecnica = datos.materialesTecnica!!.trim()
        artesania.precioBase = datos.precioBase!!
        artesania.stock = datos.stock!!
    }

    private fun buscar(id: Long): Artesania =
        artesaniaRepository.findById(id).orElseThrow { NoEncontradoException("Artesanía no encontrada") }

    private fun buscarCategoria(id: Long): Categoria =
        categoriaRepository.findById(id).orElseThrow { NoEncontradoException("La categoría no existe") }
}
