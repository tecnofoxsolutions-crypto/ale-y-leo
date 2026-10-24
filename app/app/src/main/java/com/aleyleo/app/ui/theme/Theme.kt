package com.aleyleo.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Colores de la marca asignados a los roles de Material 3.
private val ColoresAleYLeo = lightColorScheme(
    primary = Cobre,
    onPrimary = Blanco,
    primaryContainer = CobreClaro,
    secondary = Lapislazuli,
    onSecondary = Blanco,
    secondaryContainer = LapislazuliClaro,
    background = Ceramica,
    onBackground = Tinta,
    surface = Blanco,
    onSurface = Tinta
)

@Composable
fun AleYLeoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColoresAleYLeo,
        typography = Typography,
        content = content
    )
}
