package com.aleyleo.app.repository

import com.aleyleo.app.dao.FavoritoDao
import com.aleyleo.app.model.Favorito
import kotlinx.coroutines.flow.Flow

// Repositorio de favoritos: usa Room (datos locales del teléfono).
class FavoritoRepository(
    private val favoritoDao: FavoritoDao
) {
    val favoritos: Flow<List<Favorito>> = favoritoDao.favoritosListar()

    suspend fun favoritoAlmacenar(favorito: Favorito) {
        favoritoDao.favoritoAlmacenar(favorito)
    }

    suspend fun favoritoEliminar(artesaniaId: Int) {
        favoritoDao.favoritoEliminar(artesaniaId)
    }

    suspend fun favoritosVaciar() {
        favoritoDao.favoritosVaciar()
    }
}
