package com.aleyleo.app.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aleyleo.app.model.ItemCarro
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemCarroDao {
    // Sirve para agregar un producto nuevo o actualizar su cantidad.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun itemAlmacenar(item: ItemCarro)

    @Query("DELETE FROM item_carro WHERE artesaniaId = :artesaniaId")
    suspend fun itemEliminar(artesaniaId: Int)

    @Query("DELETE FROM item_carro")
    suspend fun carroVaciar()

    @Query("SELECT * FROM item_carro WHERE artesaniaId = :artesaniaId")
    suspend fun itemObtener(artesaniaId: Int): ItemCarro?

    @Query("SELECT * FROM item_carro ORDER BY nombre ASC")
    fun itemsListar(): Flow<List<ItemCarro>>
}
