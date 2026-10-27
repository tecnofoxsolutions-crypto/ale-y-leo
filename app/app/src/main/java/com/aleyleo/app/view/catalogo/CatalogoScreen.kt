package com.aleyleo.app.view.catalogo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.model.Categoria
import com.aleyleo.app.view.componentes.EstadoVacio
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.view.componentes.TarjetaArtesania
import com.aleyleo.app.viewmodel.CatalogoViewModel
import com.aleyleo.app.viewmodel.FavoritosViewModel
import com.aleyleo.app.viewmodel.QrViewModel
import com.aleyleo.app.viewmodel.ResultadoQr
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.launch
import com.aleyleo.app.R

// P04 Catálogo: buscador, chips de categoría y grilla de artesanías.
@Composable
fun CatalogoScreen(
    onVerDetalle: (Int) -> Unit,
    catalogoViewModel: CatalogoViewModel = viewModel(),
    favoritosViewModel: FavoritosViewModel = viewModel(),
    qrViewModel: QrViewModel = viewModel()
) {
    val uiState by catalogoViewModel.uiState.collectAsStateWithLifecycle()
    val idsFavoritos by favoritosViewModel.idsFavoritos.collectAsStateWithLifecycle()
    val busqueda by catalogoViewModel.busqueda.collectAsStateWithLifecycle()

    // Al cambiar el chip o la búsqueda, la grilla vuelve al principio.
    val estadoGrilla = rememberLazyGridState()
    LaunchedEffect(uiState.categoriaSeleccionada, busqueda) {
        estadoGrilla.scrollToItem(0)
    }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Lector de QR de Google (visto en clase): abre la cámara y devuelve el texto del código.
    // No hay que pedir el permiso de cámara: lo maneja Google Play Services.
    val opcionesQr = remember {
        GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    }
    val scanner = remember(context, opcionesQr) {
        GmsBarcodeScanning.getClient(context, opcionesQr)
    }

    fun escanearQr() {
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                when (val resultado = qrViewModel.guardarQR(barcode.rawValue.orEmpty())) {
                    is ResultadoQr.AbrirArtesania -> onVerDetalle(resultado.id)
                    is ResultadoQr.Error -> scope.launch { snackbarHostState.showSnackbar(resultado.mensaje) }
                }
            }
            .addOnCanceledListener {
                // Escaneo cancelado: se queda en el Catálogo sin hacer nada.
            }
            .addOnFailureListener { error ->
                scope.launch { snackbarHostState.showSnackbar("No se pudo abrir el lector: ${error.message}") }
            }
    }

    PantallaBase(
        titulo = "Catálogo",
        desplazable = false,
        snackbarHostState = snackbarHostState,
        acciones = {
            TextButton(onClick = { escanearQr() }) {
                Icon(Icons.Filled.QrCodeScanner, contentDescription = null)
                Text(" Escanear QR")
            }
        }
    ) {

        Buscador(
            texto = busqueda,
            onTextoChange = { catalogoViewModel.cambiarBusqueda(it) }
        )

        ChipsCategorias(
            categorias = uiState.categorias,
            seleccionada = uiState.categoriaSeleccionada,
            onSeleccionar = { catalogoViewModel.seleccionarCategoria(it) }
        )

        if (uiState.artesanias.isEmpty()) {
            EstadoVacio(mensaje = "No encontramos artesanías con ese filtro", ilustracion = R.drawable.ilustracion_lupa)
        } else {
            // Grilla de 2 columnas que solo dibuja las tarjetas visibles (lazy).
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = estadoGrilla,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.artesanias, key = { it.id }) { artesania ->
                    TarjetaArtesania(
                        artesania = artesania,
                        esFavorito = artesania.id in idsFavoritos,
                        onClick = { onVerDetalle(artesania.id) },
                        onFavoritoClick = { favoritosViewModel.alternarFavorito(artesania) }
                    )
                }
            }
        }
    }
}

@Composable
private fun Buscador(texto: String, onTextoChange: (String) -> Unit) {
    OutlinedTextField(
        value = texto,
        onValueChange = onTextoChange,
        placeholder = { Text("Buscar por nombre o material") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (texto.isNotEmpty()) {
                IconButton(onClick = { onTextoChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Borrar búsqueda")
                }
            }
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

// Fila deslizable con el chip "Todas" + un chip por categoría.
@Composable
private fun ChipsCategorias(
    categorias: List<Categoria>,
    seleccionada: Int?,
    onSeleccionar: (Int?) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            FilterChip(
                selected = seleccionada == null,
                onClick = { onSeleccionar(null) },
                label = { Text("Todas") }
            )
        }
        items(categorias, key = { it.id }) { categoria ->
            FilterChip(
                selected = seleccionada == categoria.id,
                onClick = { onSeleccionar(categoria.id) },
                label = { Text(categoria.nombre) }
            )
        }
    }
}
