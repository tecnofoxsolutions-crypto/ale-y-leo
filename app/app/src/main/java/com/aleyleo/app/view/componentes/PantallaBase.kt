package com.aleyleo.app.view.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aleyleo.app.ui.theme.AleYLeoTheme

// Estructura común de las pantallas: barra superior con título
// (y flecha "volver" si se pasa onVolver) + contenido en columna.
// desplazable = false para pantallas con su propia lista (LazyColumn o LazyVerticalGrid),
// porque una lista con scroll no puede ir dentro de otra columna con scroll.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBase(
    titulo: String,
    onVolver: (() -> Unit)? = null,
    desplazable: Boolean = true,
    snackbarHostState: SnackbarHostState? = null, // para mostrar mensajes breves abajo
    contenido: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        snackbarHost = {
            if (snackbarHostState != null) {
                SnackbarHost(snackbarHostState)
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    if (onVolver != null) {
                        IconButton(onClick = onVolver) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        val scroll = if (desplazable) Modifier.verticalScroll(rememberScrollState()) else Modifier

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding() // deja espacio para el teclado: así se puede llegar a los botones de abajo
                .then(scroll)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = contenido
        )
    }
}

@Preview
@Composable
fun PantallaBasePreview() {
    AleYLeoTheme {
        PantallaBase(titulo = "Título de ejemplo", onVolver = {}) {
            Text("Contenido de la pantalla")
        }
    }
}

// Texto temporal que indica qué tarea completa la pantalla.
@Composable
fun AvisoEnConstruccion(codigo: String, tarea: String) {
    Text(
        text = "$codigo · pantalla base, se completa en $tarea",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
