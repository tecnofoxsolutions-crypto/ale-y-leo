package com.aleyleo.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aleyleo.app.database.AppDatabase
import com.aleyleo.app.model.Artesania
import com.aleyleo.app.model.Favorito
import com.aleyleo.app.repository.CatalogoRepository
import com.aleyleo.app.repository.FavoritoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ViewModel de favoritos. Igual que el ejemplo del profesor:
// AndroidViewModel porque necesita el contexto para abrir la base de datos Room.
class FavoritosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FavoritoRepository

    // Ids de las artesanías favoritas: sirve para pintar los corazones.
    val idsFavoritos: StateFlow<Set<Int>>

    // Las artesanías completas que están en favoritos (para la pestaña Favoritos).
    val artesaniasFavoritas: StateFlow<List<Artesania>>

    init {
        val favoritoDao = AppDatabase
            .getDatabase(application)
            .favoritoDao()

        repository = FavoritoRepository(favoritoDao)

        idsFavoritos = repository.favoritos
            .map { lista -> lista.map { it.artesaniaId }.toSet() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

        // Cruza los favoritos guardados en Room con el catálogo para tener los datos actuales
        // (precio, stock). Si una artesanía se desactiva, deja de aparecer.
        artesaniasFavoritas = combine(repository.favoritos, CatalogoRepository.artesanias) { favoritos, artesanias ->
            favoritos.mapNotNull { favorito ->
                artesanias.find { it.id == favorito.artesaniaId && it.activo }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    // Si ya es favorita la quita; si no, la agrega (HU-07).
    fun alternarFavorito(artesania: Artesania) {
        viewModelScope.launch {
            if (artesania.id in idsFavoritos.value) {
                repository.favoritoEliminar(artesania.id)
            } else {
                repository.favoritoAlmacenar(
                    Favorito(
                        artesaniaId = artesania.id,
                        nombre = artesania.nombre,
                        precio = artesania.precioBase
                    )
                )
            }
        }
    }
}
