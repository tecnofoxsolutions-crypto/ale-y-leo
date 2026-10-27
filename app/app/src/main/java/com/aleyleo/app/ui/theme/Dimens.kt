package com.aleyleo.app.ui.theme

import androidx.compose.ui.unit.dp

// Espaciado y medidas fijas (concepto, sección 5.1).
object Dimens {
    val MargenPantalla = 16.dp       // margen lateral de todas las pantallas
    val EntreTarjetas = 12.dp        // separación de la grilla del catálogo
    val EntreSecciones = 24.dp       // Detalle, Checkout, formularios
    val EntreCampos = 16.dp

    val AltoBotonPrincipal = 52.dp
    val AltoBuscador = 48.dp
    val AltoChip = 32.dp
    val AltoChipEstado = 24.dp
    val AltoCabeceraCatalogo = 140.dp
    val MiniaturaCarro = 72.dp
    val BotonCantidad = 40.dp
    val IlustracionVacio = 176.dp
    val Sello = 232.dp

    // Costura: línea punteada de 1 dp, trazos de 4 dp y espacios de 4 dp.
    val CosturaGrosor = 1.dp
    val CosturaTrazo = 4.dp
    val CosturaEspacio = 4.dp

    // Tarjetas sin sombra; solo la barra inferior lleva 1 dp de elevación.
    val ElevacionBarra = 1.dp
}
