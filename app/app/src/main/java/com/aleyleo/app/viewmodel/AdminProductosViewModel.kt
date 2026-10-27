package com.aleyleo.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleyleo.app.model.Artesania
import com.aleyleo.app.repository.CatalogoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// ViewModel de P17 Admin: productos. A diferencia del catálogo del cliente,
// la admin ve TODAS las artesanías, también las inactivas (HU-16).
class AdminProductosViewModel : ViewModel() {

    private val repository = CatalogoRepository

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    val productos: StateFlow<List<Artesania>> = combine(repository.artesanias, _busqueda) { artesanias, texto ->
        filtrarProductosAdmin(artesanias, texto)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cambiarBusqueda(texto: String) {
        _busqueda.value = texto
    }

    fun nombreCategoria(categoriaId: Int): String {
        return repository.obtenerCategoria(categoriaId)?.nombre ?: ""
    }
}

// Busca por nombre o materiales e incluye las inactivas. Ordena por nombre.
fun filtrarProductosAdmin(artesanias: List<Artesania>, texto: String): List<Artesania> {
    val busqueda = texto.trim()
    return artesanias
        .filter {
            busqueda.isEmpty() ||
                it.nombre.contains(busqueda, ignoreCase = true) ||
                it.materialesTecnica.contains(busqueda, ignoreCase = true)
        }
        .sortedBy { it.nombre.lowercase() }
}
