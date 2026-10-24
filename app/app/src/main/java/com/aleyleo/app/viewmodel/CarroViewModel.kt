package com.aleyleo.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aleyleo.app.database.AppDatabase
import com.aleyleo.app.model.Artesania
import com.aleyleo.app.model.ItemCarro
import com.aleyleo.app.repository.CarroRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ViewModel del carro. Los datos viven en Room, así que todas las pantallas
// que usan este ViewModel ven el mismo carro, y se conserva al cerrar la app.
class CarroViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CarroRepository

    val items: StateFlow<List<ItemCarro>>

    init {
        val itemCarroDao = AppDatabase
            .getDatabase(application)
            .itemCarroDao()

        repository = CarroRepository(itemCarroDao)

        items = repository.items.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // Se recalculan solos cada vez que cambia la lista (HU-09).
    val total: StateFlow<Int> = items
        .map { lista -> lista.sumOf { it.subtotal } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Unidades en el carro: es el número del ícono de la barra inferior (HU-08).
    val cantidadTotal: StateFlow<Int> = items
        .map { lista -> lista.sumOf { it.cantidad } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Agrega una artesanía al carro y devuelve el mensaje para el snackbar.
    // "suspend" porque espera a que Room lea y guarde antes de responder.
    suspend fun agregar(artesania: Artesania, cantidad: Int): String {
        val actual = repository.itemObtener(artesania.id)?.cantidad ?: 0
        val nueva = calcularNuevaCantidad(actual, cantidad, artesania.stock)

        if (nueva == actual) {
            return "Ya tienes en el carro todo el stock disponible (${artesania.stock})"
        }

        repository.itemAlmacenar(
            ItemCarro(
                artesaniaId = artesania.id,
                categoriaId = artesania.categoriaId,
                nombre = artesania.nombre,
                precioUnitario = artesania.precioBase,
                cantidad = nueva,
                stockConocido = artesania.stock
            )
        )

        return if (nueva < actual + cantidad) {
            "Solo hay ${artesania.stock} en stock: tu carro quedó con $nueva"
        } else {
            "Agregado al carro"
        }
    }

    fun sumar(item: ItemCarro) {
        if (item.cantidad < item.stockConocido) {
            guardar(item.copy(cantidad = item.cantidad + 1))
        }
    }

    // La pantalla pregunta antes de llegar a 0, así que aquí nunca baja de 1.
    fun restar(item: ItemCarro) {
        if (item.cantidad > 1) {
            guardar(item.copy(cantidad = item.cantidad - 1))
        }
    }

    fun quitar(item: ItemCarro) {
        viewModelScope.launch {
            repository.itemEliminar(item.artesaniaId)
        }
    }

    private fun guardar(item: ItemCarro) {
        viewModelScope.launch {
            repository.itemAlmacenar(item)
        }
    }
}

// Suma la cantidad nueva a la que ya hay en el carro, sin pasar el stock (HU-08).
fun calcularNuevaCantidad(enCarro: Int, agregar: Int, stock: Int): Int {
    return minOf(enCarro + agregar, stock)
}
