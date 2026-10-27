package com.aleyleo.app.view.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aleyleo.app.model.EstadoPedido
import com.aleyleo.app.ui.theme.AleYLeoTheme
import com.aleyleo.app.ui.theme.estilo

// Chip con el estado del pedido. El color, el fondo y el ícono de cada estado están en
// ui/theme/Estados.kt (diseño de Emanuel). El estado nunca depende solo del color:
// siempre lleva ícono y texto.
@Composable
fun ChipEstado(estado: EstadoPedido, modifier: Modifier = Modifier) {
    val estilo = estado.estilo()

    Surface(
        color = estilo.fondo,
        contentColor = estilo.color,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(estilo.icono, contentDescription = null, modifier = Modifier.size(14.dp))
            Text(
                text = estilo.texto.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
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
