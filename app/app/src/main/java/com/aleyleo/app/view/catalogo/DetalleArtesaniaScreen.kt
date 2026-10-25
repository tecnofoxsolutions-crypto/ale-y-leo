package com.aleyleo.app.view.catalogo

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.util.formatoClp
import com.aleyleo.app.view.componentes.BotonFavorito
import com.aleyleo.app.view.componentes.EstadoVacio
import com.aleyleo.app.view.componentes.EtiquetaAgotado
import com.aleyleo.app.view.componentes.ImagenArtesania
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.viewmodel.CarroViewModel
import com.aleyleo.app.viewmodel.CatalogoViewModel
import com.aleyleo.app.viewmodel.FavoritosViewModel
import kotlinx.coroutines.launch

// P05 Detalle de artesanía: recibe el id desde la ruta "detalle_artesania/{artesaniaId}".
@Composable
fun DetalleArtesaniaScreen(
    artesaniaId: Int,
    onVolver: () -> Unit,
    onIrAlCarro: () -> Unit,
    catalogoViewModel: CatalogoViewModel = viewModel(),
    favoritosViewModel: FavoritosViewModel = viewModel(),
    carroViewModel: CarroViewModel = viewModel()
) {
    // Observa el catálogo: si el stock cambia (por ejemplo, al pagar un pedido), la pantalla se actualiza.
    val artesanias by catalogoViewModel.artesanias.collectAsStateWithLifecycle()
    val artesania = artesanias.find { it.id == artesaniaId }
    val idsFavoritos by favoritosViewModel.idsFavoritos.collectAsStateWithLifecycle()

    // Estado local de la pantalla (no es dato del negocio): la cantidad elegida.
    // rememberSaveable lo conserva aunque se gire el teléfono.
    var cantidad by rememberSaveable { mutableIntStateOf(1) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (artesania == null) {
        PantallaBase(titulo = "Detalle", onVolver = onVolver) {
            EstadoVacio(mensaje = "No encontramos esta artesanía")
        }
        return
    }

    PantallaBase(titulo = "Detalle", onVolver = onVolver, snackbarHostState = snackbarHostState) {

        ImagenArtesania(
            categoriaId = artesania.categoriaId,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = artesania.nombre,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
            BotonFavorito(
                esFavorito = artesania.id in idsFavoritos,
                onClick = { favoritosViewModel.alternarFavorito(artesania) }
            )
        }

        Text(
            text = catalogoViewModel.nombreCategoria(artesania.categoriaId),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        Text(
            text = formatoClp(artesania.precioBase),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        if (artesania.agotada) {
            EtiquetaAgotado()
        } else {
            Text("Stock disponible: ${artesania.stock}")
        }

        HorizontalDivider()

        Text("Materiales o técnica", style = MaterialTheme.typography.titleSmall)
        Text(artesania.materialesTecnica)

        Text("Descripción", style = MaterialTheme.typography.titleSmall)
        Text(artesania.descripcion)

        if (!artesania.agotada) {
            SelectorCantidad(
                cantidad = cantidad,
                maximo = artesania.stock,
                onCambiar = { cantidad = it }
            )
        }

        // Sin stock el botón queda deshabilitado (HU-06).
        Button(
            onClick = {
                scope.launch {
                    val mensaje = carroViewModel.agregar(artesania, cantidad)
                    val resultado = snackbarHostState.showSnackbar(
                        message = mensaje,
                        actionLabel = "Ver carro",
                        duration = SnackbarDuration.Short
                    )
                    if (resultado == SnackbarResult.ActionPerformed) {
                        onIrAlCarro()
                    }
                }
            },
            enabled = !artesania.agotada,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (artesania.agotada) "Agotado" else "Agregar al carro")
        }
    }
}

// Botones − cantidad + limitados entre 1 y el stock disponible (HU-08).
@Composable
private fun SelectorCantidad(cantidad: Int, maximo: Int, onCambiar: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Cantidad", modifier = Modifier.weight(1f))

        OutlinedIconButton(onClick = { onCambiar(cantidad - 1) }, enabled = cantidad > 1) {
            Icon(Icons.Filled.Remove, contentDescription = "Restar uno")
        }
        Text(
            text = "$cantidad",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        OutlinedIconButton(onClick = { onCambiar(cantidad + 1) }, enabled = cantidad < maximo) {
            Icon(Icons.Filled.Add, contentDescription = "Sumar uno")
        }
    }
}
