package com.aleyleo.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aleyleo.app.database.AppDatabase
import com.aleyleo.app.model.ItemCarro
import com.aleyleo.app.model.ItemSolicitud
import com.aleyleo.app.model.ResultadoPedido
import com.aleyleo.app.repository.AuthRepository
import com.aleyleo.app.repository.CarroRepository
import com.aleyleo.app.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

const val MAX_NOTAS = 500

// Errores de cada campo (null = sin error).
data class ErroresCheckout(
    val direccion: String? = null,
    val comuna: String? = null
) {
    val hayErrores: Boolean
        get() = direccion != null || comuna != null
}

// Estado del formulario del Checkout (P08).
data class FormularioCheckout(
    val direccion: String = "",
    val comuna: String = "",
    val notas: String = "",
    val errores: ErroresCheckout = ErroresCheckout(),
    val errorGeneral: String? = null // por ejemplo, falta de stock
)

class CheckoutViewModel(application: Application) : AndroidViewModel(application) {

    private val carroRepository: CarroRepository

    val items: StateFlow<List<ItemCarro>>

    private val _formulario = MutableStateFlow(FormularioCheckout())
    val formulario: StateFlow<FormularioCheckout> = _formulario.asStateFlow()

    init {
        val itemCarroDao = AppDatabase
            .getDatabase(application)
            .itemCarroDao()

        carroRepository = CarroRepository(itemCarroDao)

        items = carroRepository.items.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Precarga la dirección y la comuna del perfil (HU-10); el cliente las puede editar.
        val usuario = AuthRepository.sesion.value
        if (usuario != null) {
            _formulario.value = FormularioCheckout(direccion = usuario.direccion, comuna = usuario.comuna)
        }
    }

    val total: StateFlow<Int> = items
        .map { lista -> lista.sumOf { it.subtotal } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun cambiarDireccion(texto: String) {
        _formulario.update { it.copy(direccion = texto, errores = it.errores.copy(direccion = null)) }
    }

    fun cambiarComuna(texto: String) {
        _formulario.update { it.copy(comuna = texto, errores = it.errores.copy(comuna = null)) }
    }

    fun cambiarNotas(texto: String) {
        _formulario.update { it.copy(notas = texto.take(MAX_NOTAS)) } // no deja pasar de 500
    }

    // Valida, crea el pedido y vacía el carro. Devuelve el id del pedido, o null si algo falló
    // (en ese caso el formulario queda con el error para mostrarlo).
    suspend fun generarPedido(): Int? {
        val actual = _formulario.value

        val errores = validarCheckout(actual.direccion, actual.comuna)
        if (errores.hayErrores) {
            _formulario.update { it.copy(errores = errores) }
            return null
        }

        val usuario = AuthRepository.sesion.value
        if (usuario == null) {
            _formulario.update { it.copy(errorGeneral = "Debes iniciar sesión para comprar") }
            return null
        }

        // first(): lee el contenido actual del carro en Room una sola vez.
        val itemsCarro = carroRepository.items.first()
        val solicitud = itemsCarro.map { ItemSolicitud(it.artesaniaId, it.cantidad) }

        return when (val resultado = PedidoRepository.crearPedido(
            usuarioId = usuario.id,
            items = solicitud,
            direccion = actual.direccion,
            comuna = actual.comuna,
            notas = actual.notas
        )) {
            is ResultadoPedido.Exito -> {
                carroRepository.carroVaciar() // con éxito, el carro se vacía (HU-10)
                resultado.pedido.id
            }
            is ResultadoPedido.Error -> {
                // Si falta stock, el pedido no se crea y el carro se conserva.
                _formulario.update { it.copy(errorGeneral = resultado.mensaje) }
                null
            }
        }
    }
}

// Dirección y comuna obligatorias (HU-10). Las notas son opcionales.
fun validarCheckout(direccion: String, comuna: String): ErroresCheckout {
    return ErroresCheckout(
        direccion = if (direccion.isBlank()) "Ingresa la dirección de despacho" else null,
        comuna = if (comuna.isBlank()) "Ingresa la comuna" else null
    )
}
