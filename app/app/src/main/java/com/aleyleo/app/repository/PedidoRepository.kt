package com.aleyleo.app.repository

import com.aleyleo.app.datos.DatosPrueba
import com.aleyleo.app.model.DetallePedido
import com.aleyleo.app.model.EstadoPedido
import com.aleyleo.app.model.ItemSolicitud
import com.aleyleo.app.model.Pedido
import com.aleyleo.app.model.ResultadoPedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// Repositorio de pedidos. En la E2 trabaja en memoria y SIMULA las reglas del backend
// (PedidoService): validar stock, calcular el total, generar el código y cambiar estados.
// En la entrega final estas reglas las aplica el servidor y aquí solo se llama a la API.
object PedidoRepository {

    private val _pedidos = MutableStateFlow(DatosPrueba.pedidos)
    val pedidos: StateFlow<List<Pedido>> = _pedidos.asStateFlow()

    fun obtenerPedido(id: Int): Pedido? {
        return _pedidos.value.find { it.id == id }
    }

    // Pedidos de un cliente, del más nuevo al más antiguo.
    fun pedidosDeUsuario(usuarioId: Int): List<Pedido> {
        return _pedidos.value
            .filter { it.usuarioId == usuarioId }
            .sortedByDescending { it.id }
    }

    // POST /api/pedidos (HU-10): valida stock y crea el pedido en estado GENERADA.
    // El stock NO se descuenta aquí, sino al pagar.
    fun crearPedido(
        usuarioId: Int,
        items: List<ItemSolicitud>,
        direccion: String,
        comuna: String,
        notas: String
    ): ResultadoPedido {
        if (items.isEmpty()) {
            return ResultadoPedido.Error("El carro está vacío")
        }

        val detalles = mutableListOf<DetallePedido>()
        for (item in items) {
            val artesania = CatalogoRepository.obtenerArtesania(item.artesaniaId)
            if (artesania == null || !artesania.activo) {
                return ResultadoPedido.Error("Una de las artesanías del carro ya no está disponible")
            }
            if (item.cantidad > artesania.stock) {
                return ResultadoPedido.Error(
                    "No hay stock suficiente de \"${artesania.nombre}\" (quedan ${artesania.stock})"
                )
            }
            // Precio y nombre los toma del catálogo, no de lo que envía la app.
            detalles.add(DetallePedido(artesania.id, artesania.nombre, item.cantidad, artesania.precioBase))
        }

        val id = (_pedidos.value.maxOfOrNull { it.id } ?: 0) + 1
        val pedido = Pedido(
            id = id,
            codigo = String.format(Locale.US, "PED-2026-%04d", id),
            usuarioId = usuarioId,
            estado = EstadoPedido.GENERADA,
            direccionDespacho = direccion.trim(),
            comunaDespacho = comuna.trim(),
            notas = notas.trim(),
            fechaCreacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            detalles = detalles
        )
        _pedidos.update { it + pedido }
        return ResultadoPedido.Exito(pedido)
    }

    // POST /api/pedidos/{id}/pagar (HU-11): vuelve a revisar el stock y lo descuenta.
    // Si falta stock, el pedido sigue GENERADA y no se descuenta nada.
    fun pagar(pedidoId: Int): ResultadoPedido {
        val pedido = obtenerPedido(pedidoId) ?: return ResultadoPedido.Error("Pedido no encontrado")

        if (pedido.estado != EstadoPedido.GENERADA) {
            return ResultadoPedido.Error("Solo se puede pagar un pedido en estado Generada")
        }

        // Primero revisa TODO; recién después descuenta (todo o nada, como @Transactional).
        for (detalle in pedido.detalles) {
            val artesania = CatalogoRepository.obtenerArtesania(detalle.artesaniaId)
            if (artesania == null || detalle.cantidad > artesania.stock) {
                return ResultadoPedido.Error(
                    "No hay stock suficiente de \"${detalle.nombreArtesania}\". El pedido sigue Generada."
                )
            }
        }
        pedido.detalles.forEach { CatalogoRepository.descontarStock(it.artesaniaId, it.cantidad) }

        return cambiarEstado(pedido, EstadoPedido.PAGADA)
    }

    // POST /api/pedidos/{id}/cancelar (HU-13): solo desde GENERADA.
    fun cancelar(pedidoId: Int): ResultadoPedido {
        val pedido = obtenerPedido(pedidoId) ?: return ResultadoPedido.Error("Pedido no encontrado")

        if (pedido.estado != EstadoPedido.GENERADA) {
            return ResultadoPedido.Error("Solo se puede cancelar un pedido en estado Generada")
        }
        return cambiarEstado(pedido, EstadoPedido.CANCELADA)
    }

    // PATCH /api/admin/pedidos/{id}/estado (HU-15): solo transiciones válidas de la tabla 5.3.
    // Cualquier otra responde error (en el backend, 409 Conflict).
    fun cambiarEstadoAdmin(pedidoId: Int, nuevo: EstadoPedido): ResultadoPedido {
        val pedido = obtenerPedido(pedidoId) ?: return ResultadoPedido.Error("Pedido no encontrado")

        if (nuevo !in pedido.estado.siguientesParaAdmin()) {
            return ResultadoPedido.Error(
                "No se puede pasar de ${pedido.estado.etiqueta} a ${nuevo.etiqueta}"
            )
        }
        // Rechazar un pedido pagado devuelve las unidades al catálogo.
        if (nuevo == EstadoPedido.RECHAZADA) {
            pedido.detalles.forEach { CatalogoRepository.reponerStock(it.artesaniaId, it.cantidad) }
        }
        return cambiarEstado(pedido, nuevo)
    }

    private fun cambiarEstado(pedido: Pedido, nuevo: EstadoPedido): ResultadoPedido {
        val actualizado = pedido.copy(estado = nuevo)
        _pedidos.update { lista -> lista.map { if (it.id == pedido.id) actualizado else it } }
        return ResultadoPedido.Exito(actualizado)
    }

    // Vuelve a los datos iniciales (lo usan las pruebas unitarias).
    fun reiniciar() {
        _pedidos.value = DatosPrueba.pedidos
    }
}
