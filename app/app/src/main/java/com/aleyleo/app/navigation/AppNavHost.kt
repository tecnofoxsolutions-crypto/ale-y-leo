package com.aleyleo.app.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.viewmodel.CarroViewModel
import com.aleyleo.app.view.admin.AdminCategoriasScreen
import com.aleyleo.app.view.admin.AdminDetallePedidoScreen
import com.aleyleo.app.view.admin.AdminPedidosScreen
import com.aleyleo.app.view.admin.AdminProductosScreen
import com.aleyleo.app.view.admin.FormProductoScreen
import com.aleyleo.app.view.auth.LoginScreen
import com.aleyleo.app.view.auth.RegistroScreen
import com.aleyleo.app.view.auth.SplashScreen
import com.aleyleo.app.view.carro.CarroScreen
import com.aleyleo.app.view.carro.CheckoutScreen
import com.aleyleo.app.view.catalogo.CatalogoScreen
import com.aleyleo.app.view.catalogo.DetalleArtesaniaScreen
import com.aleyleo.app.view.favoritos.FavoritosScreen
import com.aleyleo.app.view.pedidos.ConfirmacionScreen
import com.aleyleo.app.view.pedidos.DetallePedidoScreen
import com.aleyleo.app.view.pedidos.MisPedidosScreen
import com.aleyleo.app.view.pedidos.PagoScreen
import com.aleyleo.app.view.perfil.EditarPerfilScreen
import com.aleyleo.app.view.perfil.PerfilScreen

// Punto central de la navegación: define los 3 grafos (auth, cliente y admin)
// y muestra la barra inferior solo en las pantallas principales de cada rol.
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    carroViewModel: CarroViewModel = viewModel()
) {
    // Cantidad de unidades en el carro, para el número sobre el ícono "Carro".
    val cantidadCarro by carroViewModel.cantidadTotal.collectAsStateWithLifecycle()

    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    val pestanas = when {
        pestanasCliente.any { it.ruta == rutaActual } -> pestanasCliente
        pestanasAdmin.any { it.ruta == rutaActual } -> pestanasAdmin
        else -> null // sin barra inferior (splash, login, detalles, formularios...)
    }

    Scaffold(
        bottomBar = {
            if (pestanas != null) {
                BarraInferior(
                    navController = navController,
                    pestanas = pestanas,
                    rutaActual = rutaActual,
                    insignias = mapOf(Rutas.CARRO to cantidadCarro)
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.GRAFO_AUTH,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            grafoAuth(navController)
            grafoCliente(navController)
            grafoAdmin(navController)
        }
    }
}

// ---------- Grafo auth: Splash, Login y Registro ----------
private fun NavGraphBuilder.grafoAuth(navController: NavHostController) {
    navigation(startDestination = Rutas.SPLASH, route = Rutas.GRAFO_AUTH) {

        composable(Rutas.SPLASH) {
            SplashScreen(
                onSinSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.SPLASH) { inclusive = true } // "volver" no regresa al splash
                    }
                },
                onCliente = { entrarAGrafo(navController, Rutas.GRAFO_CLIENTE) },
                onAdmin = { entrarAGrafo(navController, Rutas.GRAFO_ADMIN) }
            )
        }

        composable(Rutas.LOGIN) {
            LoginScreen(
                onEntrarCliente = { entrarAGrafo(navController, Rutas.GRAFO_CLIENTE) },
                onEntrarAdmin = { entrarAGrafo(navController, Rutas.GRAFO_ADMIN) },
                onIrARegistro = { navController.navigate(Rutas.REGISTRO) }
            )
        }

        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onRegistrado = { entrarAGrafo(navController, Rutas.GRAFO_CLIENTE) },
                onVolver = { navController.popBackStack() }
            )
        }
    }
}

// ---------- Grafo cliente: catálogo, carro, pedidos y perfil ----------
private fun NavGraphBuilder.grafoCliente(navController: NavHostController) {
    navigation(startDestination = Rutas.CATALOGO, route = Rutas.GRAFO_CLIENTE) {

        composable(Rutas.CATALOGO) {
            CatalogoScreen(
                onVerDetalle = { id -> navController.navigate(Rutas.detalleArtesania(id)) }
            )
        }

        composable(
            route = Rutas.DETALLE_ARTESANIA,
            arguments = listOf(navArgument(Rutas.ARG_ARTESANIA_ID) { type = NavType.IntType })
        ) { entrada ->
            val artesaniaId = entrada.arguments?.getInt(Rutas.ARG_ARTESANIA_ID) ?: 0
            DetalleArtesaniaScreen(
                artesaniaId = artesaniaId,
                onVolver = { navController.popBackStack() },
                onIrAlCarro = { irAPestana(navController, Rutas.CARRO, Rutas.CATALOGO) }
            )
        }

        composable(Rutas.FAVORITOS) {
            FavoritosScreen(
                onVerDetalle = { id -> navController.navigate(Rutas.detalleArtesania(id)) }
            )
        }

        composable(Rutas.CARRO) {
            CarroScreen(
                onContinuar = { navController.navigate(Rutas.CHECKOUT) }
            )
        }

        composable(Rutas.CHECKOUT) {
            CheckoutScreen(
                onVolver = { navController.popBackStack() },
                onPedidoGenerado = { pedidoId ->
                    navController.navigate(Rutas.pago(pedidoId)) {
                        popUpTo(Rutas.CHECKOUT) { inclusive = true } // saca el checkout de la pila
                    }
                }
            )
        }

        composable(
            route = Rutas.PAGO,
            arguments = listOf(navArgument(Rutas.ARG_PEDIDO_ID) { type = NavType.IntType })
        ) { entrada ->
            val pedidoId = entrada.arguments?.getInt(Rutas.ARG_PEDIDO_ID) ?: 0
            PagoScreen(
                pedidoId = pedidoId,
                onPagado = { id ->
                    navController.navigate(Rutas.confirmacion(id)) {
                        popUpTo(Rutas.PAGO) { inclusive = true } // después de pagar no se vuelve a la pantalla de pago
                    }
                },
                onCancelado = { salirDelFlujoDeCompra(navController, Rutas.MIS_PEDIDOS) }
            )
        }

        composable(
            route = Rutas.CONFIRMACION,
            arguments = listOf(navArgument(Rutas.ARG_PEDIDO_ID) { type = NavType.IntType })
        ) { entrada ->
            val pedidoId = entrada.arguments?.getInt(Rutas.ARG_PEDIDO_ID) ?: 0
            ConfirmacionScreen(
                pedidoId = pedidoId,
                onVerMisPedidos = { salirDelFlujoDeCompra(navController, Rutas.MIS_PEDIDOS) },
                onSeguirComprando = { salirDelFlujoDeCompra(navController, Rutas.CATALOGO) }
            )
        }

        composable(Rutas.MIS_PEDIDOS) {
            MisPedidosScreen(
                onVerPedido = { id -> navController.navigate(Rutas.detallePedido(id)) }
            )
        }

        composable(
            route = Rutas.DETALLE_PEDIDO,
            arguments = listOf(navArgument(Rutas.ARG_PEDIDO_ID) { type = NavType.IntType })
        ) { entrada ->
            val pedidoId = entrada.arguments?.getInt(Rutas.ARG_PEDIDO_ID) ?: 0
            DetallePedidoScreen(
                pedidoId = pedidoId,
                onVolver = { navController.popBackStack() },
                onPagar = { id -> navController.navigate(Rutas.pago(id)) }
            )
        }

        composable(Rutas.PERFIL) {
            PerfilScreen(
                onEditar = { navController.navigate(Rutas.EDITAR_PERFIL) },
                onCerrarSesion = { cerrarSesion(navController) }
            )
        }

        composable(Rutas.EDITAR_PERFIL) {
            EditarPerfilScreen(onVolver = { navController.popBackStack() })
        }
    }
}

// ---------- Grafo admin: pedidos, productos, categorías y perfil ----------
private fun NavGraphBuilder.grafoAdmin(navController: NavHostController) {
    navigation(startDestination = Rutas.ADMIN_PEDIDOS, route = Rutas.GRAFO_ADMIN) {

        composable(Rutas.ADMIN_PEDIDOS) {
            AdminPedidosScreen(
                onVerPedido = { id -> navController.navigate(Rutas.adminDetallePedido(id)) }
            )
        }

        composable(
            route = Rutas.ADMIN_DETALLE_PEDIDO,
            arguments = listOf(navArgument(Rutas.ARG_PEDIDO_ID) { type = NavType.IntType })
        ) { entrada ->
            val pedidoId = entrada.arguments?.getInt(Rutas.ARG_PEDIDO_ID) ?: 0
            AdminDetallePedidoScreen(
                pedidoId = pedidoId,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.ADMIN_PRODUCTOS) {
            AdminProductosScreen(
                onNuevo = { navController.navigate(Rutas.adminFormProducto(0)) },
                onEditar = { id -> navController.navigate(Rutas.adminFormProducto(id)) }
            )
        }

        composable(
            route = Rutas.ADMIN_FORM_PRODUCTO,
            arguments = listOf(navArgument(Rutas.ARG_ARTESANIA_ID) { type = NavType.IntType })
        ) { entrada ->
            val artesaniaId = entrada.arguments?.getInt(Rutas.ARG_ARTESANIA_ID) ?: 0
            FormProductoScreen(
                artesaniaId = artesaniaId,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.ADMIN_CATEGORIAS) {
            AdminCategoriasScreen()
        }

        // La admin usa las mismas pantallas de perfil, pero dentro de su grafo.
        composable(Rutas.ADMIN_PERFIL) {
            PerfilScreen(
                onEditar = { navController.navigate(Rutas.ADMIN_EDITAR_PERFIL) },
                onCerrarSesion = { cerrarSesion(navController) }
            )
        }

        composable(Rutas.ADMIN_EDITAR_PERFIL) {
            EditarPerfilScreen(onVolver = { navController.popBackStack() })
        }
    }
}

// Entra al grafo del rol y borra el grafo auth de la pila:
// así el botón "atrás" no vuelve al login.
private fun entrarAGrafo(navController: NavHostController, grafo: String) {
    navController.navigate(grafo) {
        popUpTo(Rutas.GRAFO_AUTH) { inclusive = true }
    }
}

// Al terminar o cancelar una compra: vuelve a una pestaña y DESCARTA las pantallas del flujo
// (checkout, pago, confirmación). A diferencia de irAPestana, no guarda ese estado,
// así la pestaña Carro no vuelve a mostrar la confirmación.
private fun salirDelFlujoDeCompra(navController: NavHostController, ruta: String) {
    navController.navigate(ruta) {
        popUpTo(Rutas.CATALOGO)
        launchSingleTop = true
    }
}

// Vuelve al login y limpia toda la pila de pantallas.
private fun cerrarSesion(navController: NavHostController) {
    navController.navigate(Rutas.LOGIN) {
        popUpTo(navController.graph.id) { inclusive = true }
    }
}
