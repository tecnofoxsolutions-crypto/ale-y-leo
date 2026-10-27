package com.aleyleo.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleyleo.app.model.Categoria
import com.aleyleo.app.model.ResultadoCatalogo
import com.aleyleo.app.repository.CatalogoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// Una fila de P19: la categoría y cuántas artesanías activas tiene.
data class FilaCategoria(val categoria: Categoria, val artesaniasActivas: Int)

// ViewModel de P19 Admin: categorías (HU-19).
class AdminCategoriasViewModel : ViewModel() {

    private val repository = CatalogoRepository

    val filas: StateFlow<List<FilaCategoria>> =
        combine(repository.categorias, repository.artesanias) { categorias, artesanias ->
            categorias.map { categoria ->
                FilaCategoria(categoria, artesanias.count { it.categoriaId == categoria.id && it.activo })
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // id 0 = nueva. Devuelve Error con el motivo para mostrarlo en el diálogo.
    fun guardar(id: Int, nombre: String, descripcion: String): ResultadoCatalogo {
        return repository.guardarCategoria(id, nombre, descripcion)
    }

    fun cambiarActivo(categoria: Categoria): ResultadoCatalogo {
        return repository.cambiarActivoCategoria(categoria.id, !categoria.activo)
    }
}
