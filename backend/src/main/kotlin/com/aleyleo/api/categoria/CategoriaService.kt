package com.aleyleo.api.categoria

import com.aleyleo.api.artesania.ArtesaniaRepository
import com.aleyleo.api.categoria.dto.CategoriaRequest
import com.aleyleo.api.categoria.dto.CategoriaResponse
import com.aleyleo.api.categoria.dto.aResponse
import com.aleyleo.api.common.ConflictoException
import com.aleyleo.api.common.NoEncontradoException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

// Reglas de las categorías (HU-04 y HU-19).
@Service
class CategoriaService(
    private val categoriaRepository: CategoriaRepository,
    private val artesaniaRepository: ArtesaniaRepository
) {

    // Endpoint 6 (público): solo las activas.
    @Transactional(readOnly = true)
    fun listarActivas(): List<CategoriaResponse> =
        categoriaRepository.findByActivoTrueOrderByNombreAsc().map { it.aResponse() }

    // Endpoint 14 (admin): todas, también las inactivas.
    @Transactional(readOnly = true)
    fun listarTodas(): List<CategoriaResponse> =
        categoriaRepository.findAllByOrderByNombreAsc().map { it.aResponse() }

    // Endpoint 15.
    @Transactional
    fun crear(datos: CategoriaRequest): CategoriaResponse {
        val nombre = datos.nombre!!.trim()
        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) throw nombreRepetido(nombre)

        val categoria = categoriaRepository.save(Categoria(nombre = nombre, descripcion = limpiar(datos.descripcion)))
        return categoria.aResponse()
    }

    // Endpoint 16.
    @Transactional
    fun editar(id: Long, datos: CategoriaRequest): CategoriaResponse {
        val categoria = buscar(id)
        val nombre = datos.nombre!!.trim()
        if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) throw nombreRepetido(nombre)

        categoria.nombre = nombre
        categoria.descripcion = limpiar(datos.descripcion)
        datos.activo?.let { cambiarActivo(categoria, it) }
        return categoria.aResponse()
    }

    // Endpoint 17: borrado lógico (no se borra la fila, queda inactiva).
    @Transactional
    fun desactivar(id: Long) {
        cambiarActivo(buscar(id), false)
    }

    // No se desactiva una categoría que todavía tiene artesanías activas (409).
    private fun cambiarActivo(categoria: Categoria, activo: Boolean) {
        if (!activo) {
            val activas = artesaniaRepository.countByCategoriaIdAndActivoTrue(categoria.id!!)
            if (activas > 0) {
                throw ConflictoException(
                    "CATEGORIA_CON_ARTESANIAS",
                    "No se puede desactivar: tiene $activas artesanía(s) activa(s). Desactívalas o cámbialas de categoría primero."
                )
            }
        }
        categoria.activo = activo
    }

    private fun buscar(id: Long): Categoria =
        categoriaRepository.findById(id).orElseThrow { NoEncontradoException("Categoría no encontrada") }

    private fun nombreRepetido(nombre: String) =
        ConflictoException("NOMBRE_REPETIDO", "Ya existe una categoría llamada \"$nombre\"")

    private fun limpiar(texto: String?): String? = texto?.trim()?.ifEmpty { null }
}
