package com.aleyleo.app.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aleyleo.app.model.Favorito
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritoDao {
    // REPLACE: si ya existe, lo sobrescribe en vez de fallar.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun favoritoAlmacenar(favorito: Favorito)

    @Query("DELETE FROM favorito WHERE artesaniaId = :artesaniaId")
    suspend fun favoritoEliminar(artesaniaId: Int)

    @Query("DELETE FROM favorito")
    suspend fun favoritosVaciar()

    // Flow: la lista se actualiza sola cada vez que cambia la tabla.
    @Query("SELECT * FROM favorito ORDER BY fechaGuardado DESC")
    fun favoritosListar(): Flow<List<Favorito>>
}
