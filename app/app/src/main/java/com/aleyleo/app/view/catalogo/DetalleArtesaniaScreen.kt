package com.aleyleo.app.view.catalogo

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.view.componentes.BotonFavorito
import com.aleyleo.app.view.componentes.EstadoVacio
import com.aleyleo.app.view.componentes.EtiquetaAgotado
import com.aleyleo.app.view.componentes.ImagenArtesania
import com.aleyleo.app.view.componentes.PantallaBase
import com.aleyleo.app.util.formatoClp
import com.aleyleo.app.viewmodel.CatalogoViewModel

// P05 Detalle de artesanía: recibe el id desde la ruta "detalle_artesania/{artesaniaId}".
@Composable
fun DetalleArtesaniaScreen(
    artesaniaId: Int,
    onVolver: () -> Unit,
    onIrAlCarro: () -> Unit,
    catalogoViewModel: CatalogoViewModel = viewModel()
) {
    // remember: busca la artesanía una sola vez, no en cada redibujo.
    val artesania = remember(artesaniaId) { catalogoViewModel.obtenerArtesania(artesaniaId) }

    if (artesania == null) {
        PantallaBase(titulo = "Detalle", onVolver = onVolver) {
            EstadoVacio(mensaje = "No encontramos esta artesanía")
        }
        return
    }

    PantallaBase(titulo = "Detalle", onVolver = onVolver) {

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
            BotonFavorito(esFavorito = false, onClick = { }) // AP-08
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

        // Sin stock el botón queda deshabilitado (HU-06).
        // AP-08 agrega el selector de cantidad y guarda el producto en el carro (Room).
        Button(
            onClick = onIrAlCarro,
            enabled = !artesania.agotada,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (artesania.agotada) "Agotado" else "Agregar al carro")
        }
    }
}
