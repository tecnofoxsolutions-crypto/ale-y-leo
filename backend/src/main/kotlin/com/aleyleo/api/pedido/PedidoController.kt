package com.aleyleo.api.pedido

import com.aleyleo.api.config.UsuarioAutenticado
import com.aleyleo.api.pedido.dto.CambiarEstadoRequest
import com.aleyleo.api.pedido.dto.CrearPedidoRequest
import com.aleyleo.api.pedido.dto.PedidoResponse
import com.aleyleo.api.pedido.dto.ResumenPedidosResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

// Endpoints 9 a 13 (solo CLIENTE). El cliente es siempre el del token: no puede comprar a nombre de otro.
@RestController
@RequestMapping("/api/pedidos")
class PedidoController(private val pedidoService: PedidoService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(
        @AuthenticationPrincipal usuario: UsuarioAutenticado,
        @Valid @RequestBody datos: CrearPedidoRequest
    ): PedidoResponse = pedidoService.crear(usuario.id, datos)

    @GetMapping("/mis-pedidos")
    fun misPedidos(@AuthenticationPrincipal usuario: UsuarioAutenticado): List<PedidoResponse> =
        pedidoService.misPedidos(usuario.id)

    @GetMapping("/{id}")
    fun obtener(@AuthenticationPrincipal usuario: UsuarioAutenticado, @PathVariable id: Long): PedidoResponse =
        pedidoService.obtenerPropio(usuario.id, id)

    @PostMapping("/{id}/pagar")
    fun pagar(@AuthenticationPrincipal usuario: UsuarioAutenticado, @PathVariable id: Long): PedidoResponse =
        pedidoService.pagar(usuario.id, id)

    @PostMapping("/{id}/cancelar")
    fun cancelar(@AuthenticationPrincipal usuario: UsuarioAutenticado, @PathVariable id: Long): PedidoResponse =
        pedidoService.cancelar(usuario.id, id)
}

// Endpoints 23 a 25 (solo ADMIN). Ejemplo: GET /api/admin/pedidos?estado=PAGADA
@RestController
@RequestMapping("/api/admin/pedidos")
class AdminPedidoController(private val pedidoService: PedidoService) {

    @GetMapping
    fun listar(@RequestParam(required = false) estado: EstadoPedido?): List<PedidoResponse> =
        pedidoService.listarAdmin(estado)

    @GetMapping("/{id}")
    fun obtener(@PathVariable id: Long): PedidoResponse = pedidoService.obtenerAdmin(id)

    @PatchMapping("/{id}/estado")
    fun cambiarEstado(@PathVariable id: Long, @Valid @RequestBody datos: CambiarEstadoRequest): PedidoResponse =
        pedidoService.cambiarEstadoAdmin(id, datos.estado!!)
}

// Endpoint 26 (solo ADMIN): contadores por estado para el panel.
@RestController
@RequestMapping("/api/admin/resumen")
class AdminResumenController(private val pedidoService: PedidoService) {

    @GetMapping
    fun resumen(): ResumenPedidosResponse = pedidoService.resumen()
}
