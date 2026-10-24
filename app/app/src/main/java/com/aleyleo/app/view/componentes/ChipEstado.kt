package com.aleyleo.app.view.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aleyleo.app.model.EstadoPedido
import com.aleyleo.app.ui.theme.AleYLeoTheme
import com.aleyleo.app.ui.theme.EstadoCanceladaFondo
import com.aleyleo.app.ui.theme.EstadoCanceladaTexto
import com.aleyleo.app.ui.theme.EstadoFinalizadaFondo
import com.aleyleo.app.ui.theme.EstadoFinalizadaTexto
import com.aleyleo.app.ui.theme.EstadoGeneradaFondo
import com.aleyleo.app.ui.theme.EstadoGeneradaTexto
import com.aleyleo.app.ui.theme.EstadoPagadaFondo
import com.aleyleo.app.ui.theme.EstadoPagadaTexto
import com.aleyleo.app.ui.theme.EstadoPreparacionFondo
import com.aleyleo.app.ui.theme.EstadoPreparacionTexto
import com.aleyleo.app.ui.theme.EstadoRecibidaFondo
import com.aleyleo.app.ui.theme.EstadoRecibidaTexto
import com.aleyleo.app.ui.theme.EstadoRechazadaFondo
import com.aleyleo.app.ui.theme.EstadoRechazadaTexto

// Chip de color con el estado del pedido. Los colores están en ui/theme/Color.kt.
@Composable
fun ChipEstado(estado: EstadoPedido, modifier: Modifier = Modifier) {
    val (fondo, texto) = coloresDeEstado(estado)

    Surface(
        color = fondo,
        contentColor = texto,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = estado.etiqueta,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// Devuelve (color de fondo, color de texto) para cada estado.
private fun coloresDeEstado(estado: EstadoPedido): Pair<Color, Color> {
    return when (estado) {
        EstadoPedido.GENERADA -> EstadoGeneradaFondo to EstadoGeneradaTexto
        EstadoPedido.PAGADA -> EstadoPagadaFondo to EstadoPagadaTexto
        EstadoPedido.RECIBIDA -> EstadoRecibidaFondo to EstadoRecibidaTexto
        EstadoPedido.EN_PREPARACION -> EstadoPreparacionFondo to EstadoPreparacionTexto
        EstadoPedido.FINALIZADA -> EstadoFinalizadaFondo to EstadoFinalizadaTexto
        EstadoPedido.CANCELADA -> EstadoCanceladaFondo to EstadoCanceladaTexto
        EstadoPedido.RECHAZADA -> EstadoRechazadaFondo to EstadoRechazadaTexto
    }
}

@Preview(showBackground = true)
@Composable
fun ChipEstadoPreview() {
    AleYLeoTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EstadoPedido.entries.forEach { ChipEstado(it) }
        }
    }
}
