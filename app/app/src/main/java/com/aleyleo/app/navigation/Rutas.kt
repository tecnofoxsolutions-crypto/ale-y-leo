package com.aleyleo.app.navigation

// Nombres de todas las rutas de la app, en un solo lugar.
// Las rutas con {llave} reciben un argumento (por ejemplo, el id de una artesanía).
object Rutas {

    // Grafos (grupos de pantallas)
    const val GRAFO_AUTH = "auth"
    const val GRAFO_CLIENTE = "cliente"
    const val GRAFO_ADMIN = "admin"

    // Grafo auth
    const val SPLASH = "splash"                                   // P01
    const val LOGIN = "login"                                     // P02
    const val REGISTRO = "registro"                               // P03

    // Grafo cliente
    const val CATALOGO = "catalogo"                               // P04
    const val DETALLE_ARTESANIA = "detalle_artesania/{artesaniaId}" // P05
    const val FAVORITOS = "favoritos"                             // P06
    const val CARRO = "carro"                                     // P07
    const val CHECKOUT = "checkout"                               // P08
    const val PAGO = "pago/{pedidoId}"                            // P09
    const val CONFIRMACION = "confirmacion/{pedidoId}"            // P10
    const val MIS_PEDIDOS = "mis_pedidos"                         // P11
    const val DETALLE_PEDIDO = "detalle_pedido/{pedidoId}"        // P12
    const val PERFIL = "perfil"                                   // P13
    const val EDITAR_PERFIL = "editar_perfil"                     // P14

    // Grafo admin
    const val ADMIN_PEDIDOS = "admin_pedidos"                     // P15
    const val ADMIN_DETALLE_PEDIDO = "admin_detalle_pedido/{pedidoId}" // P16
    const val ADMIN_PRODUCTOS = "admin_productos"                 // P17
    const val ADMIN_FORM_PRODUCTO = "admin_form_producto/{artesaniaId}" // P18 (id 0 = nuevo)
    const val ADMIN_CATEGORIAS = "admin_categorias"               // P19
    const val ADMIN_PERFIL = "admin_perfil"                       // P13 para la admin
    const val ADMIN_EDITAR_PERFIL = "admin_editar_perfil"         // P14 para la admin

    // Nombres de los argumentos
    const val ARG_ARTESANIA_ID = "artesaniaId"
    const val ARG_PEDIDO_ID = "pedidoId"

    // Funciones que arman la ruta real con el id: "detalle_artesania/5"
    fun detalleArtesania(id: Int) = "detalle_artesania/$id"
    fun pago(pedidoId: Int) = "pago/$pedidoId"
    fun confirmacion(pedidoId: Int) = "confirmacion/$pedidoId"
    fun detallePedido(pedidoId: Int) = "detalle_pedido/$pedidoId"
    fun adminDetallePedido(pedidoId: Int) = "admin_detalle_pedido/$pedidoId"
    fun adminFormProducto(artesaniaId: Int) = "admin_form_producto/$artesaniaId"
}
