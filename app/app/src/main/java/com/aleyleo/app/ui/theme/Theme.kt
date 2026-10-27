package com.aleyleo.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

// Tema de la app. Se usa en MainActivity:
//   enableEdgeToEdge()
//   setContent { AleYLeoTheme { AppNavHost() } }
// No usa color dinámico ni tema oscuro en la E2.
@Composable
fun AleYLeoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AleYLeoColores,
        typography = AleYLeoTipografia,
        shapes = AleYLeoFormas,
        content = content,
    )
}
