package com.aleyleo.api.pedido.dto

import com.aleyleo.api.pedido.DetallePedido
import com.aleyleo.api.pedido.EstadoPedido
import com.aleyleo.api.pedido.Pedido
import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

// ---------- Peticiones ----------

// POST /api/pedidos (HU-10). Solo se envían ids y cantidades:
// el precio, el nombre y el total los pone el servidor.
data class CrearPedidoRequest(
    @field:NotEmpty(message = "El carro está vacío")
    @field:Valid
    val items: List<ItemPedidoRequest>?,

    @field:NotBlank(message = "La dirección es obligatoria")
    @field:Size(max = 200, message = "La dirección tiene máximo 200 caracteres")
    val direccionDespacho: String?,

    @field:NotBlank(message = "La comuna es obligatoria")
    @field:Size(max = 80, message = "La comuna tiene máximo 80 caracteres")
    val comunaDespacho: String?,

    @field:Size(max = 500, message = "Las notas tienen máximo 500 caracteres")
    val notas: String? = null
)

data class ItemPedidoRequest(
    @field:NotNull(message = "Falta la artesanía")
    val artesaniaId: Long?,

    @field:NotNull(message = "Falta la cantidad")
    @field:Min(value = 1, message = "La cantidad mínima es 1")
    val cantidad: Int?
)

// PATCH /api/admin/pedidos/{id}/estado (HU-15).
data class CambiarEstadoRequest(
    @field:NotNull(message = "Indica el nuevo estado")
    val estado: EstadoPedido?
)

// ---------- Respuestas ----------

// PedidoResponse de la sección 6.3. "cliente" solo viene en las respuestas de la admin.
@JsonInclude(JsonInclude.Include.NON_NULL)
data class PedidoResponse(
    val id: Long,
    val codigo: String,
    val estado: EstadoPedido,
    val total: Int,
    val fechaCreacion: LocalDateTime,
    val fechaPago: LocalDateTime?,
    val direccionDespacho: String,
    val comunaDespacho: String,
    val notas: String?,
    val detalles: List<DetalleResponse>,
    val cliente: ClienteResumen? = null
)

data class DetalleResponse(
    val artesaniaId: Long,
    val nombre: String,
    val cantidad: Int,
    val precioUnitario: Int,
    val subtotal: Int
)

// Datos del cliente que ve la admin en el detalle (HU-14).
data class ClienteResumen(
    val id: Long,
    val rut: String,
    val nombreCompleto: String,
    val email: String,
    val telefono: String
)

// GET /api/admin/resumen: cuántos pedidos hay en cada estado (incluye los que tienen 0).
data class ResumenPedidosResponse(
    val total: Long,
    val porEstado: Map<EstadoPedido, Long>
)

fun DetallePedido.aResponse() = DetalleResponse(artesania.id!!, nombreArtesania, cantidad, precioUnitario, subtotal)

fun Pedido.aResponse(conCliente: Boolean = false) = PedidoResponse(
    id = id!!,
    codigo = codigo,
    estado = estado,
    total = total,
    fechaCreacion = fechaCreacion,
    fechaPago = fechaPago,
    direccionDespacho = direccionDespacho,
    comunaDespacho = comunaDespacho,
    notas = notas,
    detalles = detalles.map { it.aResponse() },
    cliente = if (conCliente) {
        ClienteResumen(usuario.id!!, usuario.rut, usuario.nombreCompleto, usuario.email, usuario.telefono)
    } else {
        null
    }
)
