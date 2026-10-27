package com.aleyleo.api.pedido

import com.aleyleo.api.artesania.ArtesaniaRepository
import com.aleyleo.api.common.AccesoDenegadoException
import com.aleyleo.api.common.ConflictoException
import com.aleyleo.api.common.NoEncontradoException
import com.aleyleo.api.common.StockInsuficienteException
import com.aleyleo.api.pedido.dto.CrearPedidoRequest
import com.aleyleo.api.pedido.dto.PedidoResponse
import com.aleyleo.api.pedido.dto.ResumenPedidosResponse
import com.aleyleo.api.pedido.dto.aResponse
import com.aleyleo.api.usuario.UsuarioRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

// Todas las reglas de los pedidos (sección 5.3) viven aquí, no en los controllers.
// @Transactional: si algo falla a mitad de camino, se deshace todo (estado y stock cambian juntos o nada).
@Service
class PedidoService(
    private val pedidoRepository: PedidoRepository,
    private val artesaniaRepository: ArtesaniaRepository,
    private val usuarioRepository: UsuarioRepository
) {

    // ---------- Cliente ----------

    // Endpoint 9 (BE-13, HU-10): valida stock, copia nombre y precio vigente, calcula el total
    // y genera el código. Queda GENERADA y el stock NO se descuenta todavía (se descuenta al pagar).
    @Transactional
    fun crear(usuarioId: Long, datos: CrearPedidoRequest): PedidoResponse {
        val usuario = usuarioRepository.findById(usuarioId).orElseThrow { NoEncontradoException("Usuario no encontrado") }

        val pedido = Pedido(
            codigo = "TMP-" + UUID.randomUUID().toString().take(12), // el definitivo necesita el id
            usuario = usuario,
            direccionDespacho = datos.direccionDespacho!!.trim(),
            comunaDespacho = datos.comunaDespacho!!.trim(),
            notas = datos.notas?.trim()?.ifEmpty { null }
        )

        // Si la misma artesanía viene dos veces, se suman las cantidades.
        val cantidades = datos.items!!.groupBy { it.artesaniaId!! }.mapValues { (_, items) -> items.sumOf { it.cantidad!! } }

        for ((artesaniaId, cantidad) in cantidades) {
            val artesania = artesaniaRepository.findById(artesaniaId)
                .filter { it.activo && it.categoria.activo }
                .orElseThrow { NoEncontradoException("Una de las artesanías del carro ya no está disponible") }
            if (cantidad > artesania.stock) throw StockInsuficienteException(artesania.nombre)

            pedido.agregarDetalle(
                DetallePedido(
                    artesania = artesania,
                    nombreArtesania = artesania.nombre,
                    cantidad = cantidad,
                    precioUnitario = artesania.precioBase, // precio del catálogo, no lo que envía la app
                    subtotal = cantidad * artesania.precioBase
                )
            )
        }
        pedido.total = pedido.detalles.sumOf { it.subtotal }

        val guardado = pedidoRepository.save(pedido)
        guardado.codigo = "PED-%d-%04d".format(guardado.fechaCreacion.year, guardado.id)
        return guardado.aResponse()
    }

    // Endpoint 10 (BE-16, HU-12).
    @Transactional(readOnly = true)
    fun misPedidos(usuarioId: Long): List<PedidoResponse> =
        pedidoRepository.findByUsuarioIdOrderByFechaCreacionDescIdDesc(usuarioId).map { it.aResponse() }

    // Endpoint 11 (BE-16): solo el dueño puede verlo (403 si es de otro cliente).
    @Transactional(readOnly = true)
    fun obtenerPropio(usuarioId: Long, pedidoId: Long): PedidoResponse = buscarPropio(usuarioId, pedidoId).aResponse()

    // Endpoint 12 (BE-14, HU-11): pago simulado. Primero revisa TODO el stock y recién después descuenta.
    // Si falta stock: 409 y el pedido sigue GENERADA sin ningún cambio.
    @Transactional
    fun pagar(usuarioId: Long, pedidoId: Long): PedidoResponse {
        val pedido = buscarPropio(usuarioId, pedidoId)
        exigirEstado(pedido, EstadoPedido.GENERADA, "Solo se puede pagar un pedido en estado GENERADA")

        for (detalle in pedido.detalles) {
            if (detalle.cantidad > detalle.artesania.stock) throw StockInsuficienteException(detalle.nombreArtesania)
        }
        pedido.detalles.forEach { it.artesania.stock -= it.cantidad }

        pedido.fechaPago = LocalDateTime.now()
        return cambiarEstado(pedido, EstadoPedido.PAGADA).aResponse()
    }

    // Endpoint 13 (BE-15, HU-13): solo desde GENERADA y sin efecto en el stock.
    @Transactional
    fun cancelar(usuarioId: Long, pedidoId: Long): PedidoResponse {
        val pedido = buscarPropio(usuarioId, pedidoId)
        exigirEstado(pedido, EstadoPedido.GENERADA, "Solo se puede cancelar un pedido en estado GENERADA")
        return cambiarEstado(pedido, EstadoPedido.CANCELADA).aResponse()
    }

    // ---------- Admin ----------

    // Endpoint 23 (BE-16, HU-14): todos, o filtrados por estado; más nuevos primero.
    @Transactional(readOnly = true)
    fun listarAdmin(estado: EstadoPedido?): List<PedidoResponse> {
        val pedidos = if (estado == null) {
            pedidoRepository.findAllByOrderByFechaCreacionDescIdDesc()
        } else {
            pedidoRepository.findByEstadoOrderByFechaCreacionDescIdDesc(estado)
        }
        return pedidos.map { it.aResponse(conCliente = true) }
    }

    // Endpoint 24 (BE-16): detalle con los datos del cliente.
    @Transactional(readOnly = true)
    fun obtenerAdmin(pedidoId: Long): PedidoResponse = buscar(pedidoId).aResponse(conCliente = true)

    // Endpoint 25 (BE-15, HU-15): solo las transiciones de la tabla 5.3; cualquier otra, 409.
    // Rechazar devuelve las unidades al stock (ya se habían descontado al pagar).
    @Transactional
    fun cambiarEstadoAdmin(pedidoId: Long, nuevo: EstadoPedido): PedidoResponse {
        val pedido = buscar(pedidoId)
        if (nuevo !in pedido.estado.siguientesParaAdmin()) {
            throw ConflictoException("TRANSICION_INVALIDA", "No se puede pasar de ${pedido.estado} a $nuevo")
        }
        if (nuevo == EstadoPedido.RECHAZADA) {
            pedido.detalles.forEach { it.artesania.stock += it.cantidad }
        }
        return cambiarEstado(pedido, nuevo).aResponse(conCliente = true)
    }

    // Endpoint 26 (BE-16): contadores para el panel de la admin.
    @Transactional(readOnly = true)
    fun resumen(): ResumenPedidosResponse {
        val conteo = pedidoRepository.contarPorEstado().associate { (it[0] as EstadoPedido) to (it[1] as Long) }
        val porEstado = EstadoPedido.entries.associateWith { conteo[it] ?: 0L }
        return ResumenPedidosResponse(total = porEstado.values.sum(), porEstado = porEstado)
    }

    // ---------- Ayudas ----------

    private fun buscar(pedidoId: Long): Pedido =
        pedidoRepository.findById(pedidoId).orElseThrow { NoEncontradoException("Pedido no encontrado") }

    private fun buscarPropio(usuarioId: Long, pedidoId: Long): Pedido {
        val pedido = buscar(pedidoId)
        if (pedido.usuario.id != usuarioId) throw AccesoDenegadoException("Este pedido no es tuyo")
        return pedido
    }

    private fun exigirEstado(pedido: Pedido, esperado: EstadoPedido, mensaje: String) {
        if (pedido.estado != esperado) throw ConflictoException("TRANSICION_INVALIDA", mensaje)
    }

    private fun cambiarEstado(pedido: Pedido, nuevo: EstadoPedido): Pedido {
        pedido.estado = nuevo
        pedido.fechaActualizacion = LocalDateTime.now()
        return pedido
    }
}
