package com.aleyleo.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleyleo.app.model.Artesania
import com.aleyleo.app.model.Categoria
import com.aleyleo.app.repository.CatalogoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// Todo lo que la pantalla del catálogo necesita para dibujarse.
data class CatalogoUiState(
    val categorias: List<Categoria> = emptyList(),
    val categoriaSeleccionada: Int? = null, // null = chip "Todas"
    val artesanias: List<Artesania> = emptyList()
)

// ViewModel del Catálogo (P04) y del Detalle (P05).
// La pantalla solo muestra datos y avisa eventos; los filtros se aplican aquí.
class CatalogoViewModel : ViewModel() {

    private val repository = CatalogoRepository

    // Todas las artesanías sin filtrar (el Detalle la observa para ver el stock al día).
    val artesanias: StateFlow<List<Artesania>> = repository.artesanias

    private val _categoriaSeleccionada = MutableStateFlow<Int?>(null)

    // El texto del buscador va en su propio StateFlow para que el campo responda al instante.
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    // combine: cada vez que cambia una de las 4 fuentes, recalcula el estado completo.
    val uiState: StateFlow<CatalogoUiState> = combine(
        repository.categorias,
        repository.artesanias,
        _categoriaSeleccionada,
        _busqueda
    ) { categorias, artesanias, categoriaId, texto ->
        val categoriasActivas = categorias.filter { it.activo }
        CatalogoUiState(
            categorias = categoriasActivas,
            categoriaSeleccionada = categoriaId,
            artesanias = filtrarCatalogo(artesanias, categoriasActivas, categoriaId, texto)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CatalogoUiState()
    )

    fun seleccionarCategoria(categoriaId: Int?) {
        _categoriaSeleccionada.value = categoriaId
    }

    fun cambiarBusqueda(texto: String) {
        _busqueda.value = texto
    }

    fun obtenerArtesania(id: Int): Artesania? {
        return repository.obtenerArtesania(id)
    }

    fun nombreCategoria(categoriaId: Int): String {
        return repository.obtenerCategoria(categoriaId)?.nombre ?: ""
    }
}

// Reglas de HU-04 y HU-05:
// - solo artesanías activas de categorías activas
// - si hay chip elegido, solo esa categoría
// - el texto busca en nombre, descripción o materiales, sin distinguir mayúsculas
fun filtrarCatalogo(
    artesanias: List<Artesania>,
    categoriasActivas: List<Categoria>,
    categoriaId: Int?,
    texto: String
): List<Artesania> {
    val idsActivas = categoriasActivas.map { it.id }
    val busqueda = texto.trim()

    return artesanias.filter { artesania ->
        val estaActiva = artesania.activo && artesania.categoriaId in idsActivas
        val coincideCategoria = categoriaId == null || artesania.categoriaId == categoriaId
        val coincideTexto = busqueda.isEmpty() ||
            artesania.nombre.contains(busqueda, ignoreCase = true) ||
            artesania.descripcion.contains(busqueda, ignoreCase = true) ||
            artesania.materialesTecnica.contains(busqueda, ignoreCase = true)

        estaActiva && coincideCategoria && coincideTexto
    }
}
