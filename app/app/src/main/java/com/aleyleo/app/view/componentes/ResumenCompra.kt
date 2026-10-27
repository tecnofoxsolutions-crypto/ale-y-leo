package com.aleyleo.app.view.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aleyleo.app.ui.theme.AleYLeoTheme
import com.aleyleo.app.util.formatoClp

// Una línea del resumen: "Paila de Cobre x 2 ......... $28.000"
data class LineaResumen(
    val nombre: String,
    val cantidad: Int,
    val subtotal: Int
)

// Resumen de productos y total. Lo usan Checkout, Pago, Confirmación y Detalle del pedido.
@Composable
fun ResumenCompra(lineas: List<LineaResumen>, total: Int, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            lineas.forEach { linea ->
                Row {
                    Text(
                        text = "${linea.nombre} x ${linea.cantidad}",
                        modifier = Modifier.weight(1f)
                    )
                    Text(formatoClp(linea.subtotal))
                }
            }

            HorizontalDivider()

            Row {
                Text("Total", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Text(
                    text = formatoClp(total),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview
@Composable
fun ResumenCompraPreview() {
    AleYLeoTheme {
        ResumenCompra(
            lineas = listOf(
                LineaResumen("Colgante Lapislázuli Cordillera", 1, 28500),
                LineaResumen("Jarro de Greda", 2, 27000)
            ),
            total = 55500
        )
    }
}
