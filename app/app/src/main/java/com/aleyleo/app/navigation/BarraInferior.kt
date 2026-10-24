package com.aleyleo.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController

// Una pestaña de la barra inferior.
data class ItemBarra(
    val ruta: String,
    val etiqueta: String,
    val icono: ImageVector
)

val pestanasCliente = listOf(
    ItemBarra(Rutas.CATALOGO, "Catálogo", Icons.Filled.Storefront),
    ItemBarra(Rutas.FAVORITOS, "Favoritos", Icons.Filled.Favorite),
    ItemBarra(Rutas.CARRO, "Carro", Icons.Filled.ShoppingCart),
    ItemBarra(Rutas.MIS_PEDIDOS, "Pedidos", Icons.AutoMirrored.Filled.ReceiptLong),
    ItemBarra(Rutas.PERFIL, "Perfil", Icons.Filled.Person)
)

val pestanasAdmin = listOf(
    ItemBarra(Rutas.ADMIN_PEDIDOS, "Pedidos", Icons.AutoMirrored.Filled.ReceiptLong),
    ItemBarra(Rutas.ADMIN_PRODUCTOS, "Productos", Icons.Filled.Inventory2),
    ItemBarra(Rutas.ADMIN_CATEGORIAS, "Categorías", Icons.Filled.Category),
    ItemBarra(Rutas.ADMIN_PERFIL, "Perfil", Icons.Filled.Person)
)

@Composable
fun BarraInferior(
    navController: NavHostController,
    pestanas: List<ItemBarra>,
    rutaActual: String?,
    insignias: Map<String, Int> = emptyMap() // número sobre el ícono, por ruta (ej. carro)
) {
    NavigationBar {
        pestanas.forEach { pestana ->
            NavigationBarItem(
                selected = rutaActual == pestana.ruta,
                onClick = { irAPestana(navController, pestana.ruta, pestanas.first().ruta) },
                icon = {
                    val numero = insignias[pestana.ruta] ?: 0
                    // BadgedBox pone un círculo con el número sobre el ícono.
                    BadgedBox(
                        badge = {
                            if (numero > 0) {
                                Badge { Text("$numero") }
                            }
                        }
                    ) {
                        Icon(pestana.icono, contentDescription = pestana.etiqueta)
                    }
                },
                label = { Text(pestana.etiqueta) }
            )
        }
    }
}

// Cambia de pestaña sin apilar pantallas repetidas:
// vuelve a la pestaña inicial, guarda el estado de la que deja y restaura la nueva.
fun irAPestana(navController: NavHostController, ruta: String, rutaInicio: String) {
    navController.navigate(ruta) {
        popUpTo(rutaInicio) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
