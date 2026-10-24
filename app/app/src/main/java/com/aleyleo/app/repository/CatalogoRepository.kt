package com.aleyleo.app.repository

import com.aleyleo.app.datos.DatosPrueba
import com.aleyleo.app.model.Artesania
import com.aleyleo.app.model.Categoria
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Repositorio del catálogo. En la E2 lee de DatosPrueba (en memoria);
// en la entrega final, por dentro llamará a la API con Retrofit.
// Es un "object" para que todas las pantallas compartan los mismos datos.
object CatalogoRepository {

    private val _categorias = MutableStateFlow(DatosPrueba.categorias)
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    private val _artesanias = MutableStateFlow(DatosPrueba.artesanias)
    val artesanias: StateFlow<List<Artesania>> = _artesanias.asStateFlow()

    fun obtenerArtesania(id: Int): Artesania? {
        return _artesanias.value.find { it.id == id }
    }

    fun obtenerCategoria(id: Int): Categoria? {
        return _categorias.value.find { it.id == id }
    }
}
