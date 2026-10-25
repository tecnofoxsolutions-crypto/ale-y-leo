package com.aleyleo.app.view.carro

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.view.componentes.LineaResumen
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.view.componentes.ResumenCompra
import com.aleyleo.app.viewmodel.CheckoutViewModel
import com.aleyleo.app.viewmodel.MAX_NOTAS
import kotlinx.coroutines.launch

// P08 Checkout: resumen del carro, dirección de despacho y notas.
@Composable
fun CheckoutScreen(
    onVolver: () -> Unit,
    onPedidoGenerado: (Int) -> Unit,
    checkoutViewModel: CheckoutViewModel = viewModel()
) {
    val items by checkoutViewModel.items.collectAsStateWithLifecycle()
    val total by checkoutViewModel.total.collectAsStateWithLifecycle()
    val formulario by checkoutViewModel.formulario.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    var enviando by remember { mutableStateOf(false) } // evita tocar "Generar pedido" dos veces

    PantallaBase(titulo = "Confirmar pedido", onVolver = onVolver) {

        Text("Resumen", style = MaterialTheme.typography.titleMedium)
        ResumenCompra(
            lineas = items.map { LineaResumen(it.nombre, it.cantidad, it.subtotal) },
            total = total
        )

        Text("Despacho", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = formulario.direccion,
            onValueChange = { checkoutViewModel.cambiarDireccion(it) },
            label = { Text("Dirección") },
            isError = formulario.errores.direccion != null,
            supportingText = { formulario.errores.direccion?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = formulario.comuna,
            onValueChange = { checkoutViewModel.cambiarComuna(it) },
            label = { Text("Comuna") },
            isError = formulario.errores.comuna != null,
            supportingText = { formulario.errores.comuna?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = formulario.notas,
            onValueChange = { checkoutViewModel.cambiarNotas(it) },
            label = { Text("Notas (opcional)") },
            placeholder = { Text("Ejemplo: envolver para regalo") },
            supportingText = { Text("${formulario.notas.length} / $MAX_NOTAS") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        // Error del "servidor", por ejemplo falta de stock (HU-10).
        formulario.errorGeneral?.let { mensaje ->
            Text(mensaje, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                enviando = true
                scope.launch {
                    val pedidoId = checkoutViewModel.generarPedido()
                    enviando = false
                    if (pedidoId != null) {
                        onPedidoGenerado(pedidoId)
                    }
                }
            },
            enabled = items.isNotEmpty() && !enviando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generar pedido")
        }
    }
}
