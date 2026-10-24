package com.aleyleo.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aleyleo.app.dao.FavoritoDao
import com.aleyleo.app.dao.ItemCarroDao
import com.aleyleo.app.model.Favorito
import com.aleyleo.app.model.ItemCarro

// Base de datos local del teléfono. Solo guarda favoritos y carro;
// el catálogo y los pedidos vienen del backend (en la E2, de DatosPrueba).
@Database(
    entities = [Favorito::class, ItemCarro::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritoDao(): FavoritoDao
    abstract fun itemCarroDao(): ItemCarroDao

    companion object {
        // Una sola instancia para toda la app (patrón singleton).
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "db_aleyleo"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}
