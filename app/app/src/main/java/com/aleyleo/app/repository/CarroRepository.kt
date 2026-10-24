package com.aleyleo.app.repository

import com.aleyleo.app.dao.ItemCarroDao
import com.aleyleo.app.model.ItemCarro
import kotlinx.coroutines.flow.Flow

// Repositorio del carro: usa Room para que el carro sobreviva al cierre de la app.
class CarroRepository(
    private val itemCarroDao: ItemCarroDao
) {
    val items: Flow<List<ItemCarro>> = itemCarroDao.itemsListar()

    suspend fun itemAlmacenar(item: ItemCarro) {
        itemCarroDao.itemAlmacenar(item)
    }

    suspend fun itemObtener(artesaniaId: Int): ItemCarro? {
        return itemCarroDao.itemObtener(artesaniaId)
    }

    suspend fun itemEliminar(artesaniaId: Int) {
        itemCarroDao.itemEliminar(artesaniaId)
    }

    suspend fun carroVaciar() {
        itemCarroDao.carroVaciar()
    }
}
