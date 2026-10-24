package com.aleyleo.app.view.carro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.model.ItemCarro
import com.aleyleo.app.util.formatoClp
import com.aleyleo.app.view.componentes.EstadoVacio
import com.aleyleo.app.view.componentes.ImagenArtesania
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.viewmodel.CarroViewModel

// P07 Carro: productos guardados en Room, cantidades, subtotales y total.
@Composable
fun CarroScreen(
    onContinuar: () -> Unit,
    carroViewModel: CarroViewModel = viewModel()
) {
    val items by carroViewModel.items.collectAsStateWithLifecycle()
    val total by carroViewModel.total.collectAsStateWithLifecycle()

    // Ítem que se quiere quitar (null = no hay diálogo abierto).
    var itemAQuitar by remember { mutableStateOf<ItemCarro?>(null) }

    PantallaBase(titulo = "Carro", desplazable = false) {

        if (items.isEmpty()) {
            EstadoVacio(
                mensaje = "Tu carro está vacío.\nAgrega artesanías desde el catálogo.",
                icono = Icons.Filled.RemoveShoppingCart,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(items, key = { it.artesaniaId }) { item ->
                    FilaCarro(
                        item = item,
                        onSumar = { carroViewModel.sumar(item) },
                        // En 1, el "−" pregunta antes de quitar el producto (HU-09).
                        onRestar = {
                            if (item.cantidad == 1) itemAQuitar = item else carroViewModel.restar(item)
                        },
                        onQuitar = { itemAQuitar = item }
                    )
                }
            }
        }

        HorizontalDivider()

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Total", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            Text(
                text = formatoClp(total),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Button(
            onClick = onContinuar,
            enabled = items.isNotEmpty(), // carro vacío: no se puede continuar
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar")
        }
    }

    // Diálogo de confirmación para quitar un producto.
    itemAQuitar?.let { item ->
        AlertDialog(
            onDismissRequest = { itemAQuitar = null },
            title = { Text("¿Quitar del carro?") },
            text = { Text("Se quitará \"${item.nombre}\" de tu carro.") },
            confirmButton = {
                TextButton(onClick = {
                    carroViewModel.quitar(item)
                    itemAQuitar = null
                }) { Text("Quitar") }
            },
            dismissButton = {
                TextButton(onClick = { itemAQuitar = null }) { Text("Cancelar") }
            }
        )
    }
}

// Una fila del carro: imagen, nombre, precio unitario, cantidad +/- y subtotal.
@Composable
private fun FilaCarro(
    item: ItemCarro,
    onSumar: () -> Unit,
    onRestar: () -> Unit,
    onQuitar: () -> Unit
) {
    Card {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImagenArtesania(
                categoriaId = item.categoriaId,
                modifier = Modifier.size(72.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(item.nombre, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "${formatoClp(item.precioUnitario)} c/u",
                    style = MaterialTheme.typography.bodySmall
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onRestar) {
                        Icon(Icons.Filled.Remove, contentDescription = "Restar uno")
                    }
                    Text("${item.cantidad}", style = MaterialTheme.typography.titleMedium)
                    // "+" se deshabilita al llegar al stock (HU-09).
                    IconButton(onClick = onSumar, enabled = item.cantidad < item.stockConocido) {
                        Icon(Icons.Filled.Add, contentDescription = "Sumar uno")
                    }
                }

                Text(
                    text = "Subtotal: ${formatoClp(item.subtotal)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onQuitar) {
                Icon(Icons.Filled.Delete, contentDescription = "Quitar del carro")
            }
        }
    }
}
