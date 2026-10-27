package com.aleyleo.api.pedido

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PedidoRepository : JpaRepository<Pedido, Long> {

    // GET /api/pedidos/mis-pedidos (HU-12): los del cliente, del más nuevo al más antiguo.
    fun findByUsuarioIdOrderByFechaCreacionDesc(usuarioId: Long): List<Pedido>

    // GET /api/admin/pedidos (HU-14): todos, o filtrados por estado.
    fun findAllByOrderByFechaCreacionDesc(): List<Pedido>

    fun findByEstadoOrderByFechaCreacionDesc(estado: EstadoPedido): List<Pedido>

    // GET /api/admin/resumen: cuántos pedidos hay en cada estado.
    // Devuelve filas [estado, cantidad]; los estados sin pedidos no aparecen.
    @Query("SELECT p.estado, COUNT(p) FROM Pedido p GROUP BY p.estado")
    fun contarPorEstado(): List<Array<Any>>
}
