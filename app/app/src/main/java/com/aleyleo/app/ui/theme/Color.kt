package com.aleyleo.app.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Paleta «La mesa del taller». Cada color sale de un material del catálogo.
// Contrastes medidos con WCAG 2.1; todos los pares de texto cumplen AA (4,5:1).

// Cobre: botón principal, precio, pestaña activa. 5,56:1 con blanco.
val Cobre = Color(0xFFA84E24)
val CobreClaro = Color(0xFFF6DCCB)
val CobreProfundo = Color(0xFF3A1604)

// Lapislázuli: enlaces, favorito activo, avisos. 8,31:1 con blanco.
val Lapislazuli = Color(0xFF1F4E8C)
val LapislazuliClaro = Color(0xFFDCE6F3)
val LapislazuliProfundo = Color(0xFF0B2546)

// Ocre de lana: stock bajo («Quedan 2»). 6,35:1 con blanco.
val Ocre = Color(0xFF7A5A1C)
val OcreClaro = Color(0xFFF3E6C8)
val OcreProfundo = Color(0xFF2B1D00)

// Fondos y texto: lino, arcilla y greda.
val Lino = Color(0xFFF7F2EB)            // fondo de todas las pantallas
val LinoBrillante = Color(0xFFFBF8F3)
val ArcillaSuave = Color(0xFFF3EDE4)
val ArcillaClara = Color(0xFFEFE6DA)    // tarjetas, campos, barra inferior
val ArcillaMedia = Color(0xFFE5DACB)
val GredaNegra = Color(0xFF2A2420)      // texto principal. 13,75:1 con lino
val GredaMedia = Color(0xFF5E5249)      // texto secundario. 6,78:1 con lino
val GredaInversa = Color(0xFF3B332D)
val Borde = Color(0xFF8A7D71)           // bordes de campos y costura
val BordeSuave = Color(0xFFD6CABC)

val ErrorRojo = Color(0xFFB3261E)
val ErrorClaro = Color(0xFFF9DEDC)
val ErrorProfundo = Color(0xFF410E0B)

// Tema claro. El tema oscuro queda fuera de la E2 (concepto, sección 3).
// La app no usa color dinámico (Material You): la paleta es la marca.
val AleYLeoColores = lightColorScheme(
    primary = Cobre,
    onPrimary = Color.White,
    primaryContainer = CobreClaro,
    onPrimaryContainer = CobreProfundo,
    inversePrimary = Color(0xFFFFB68F),

    secondary = Lapislazuli,
    onSecondary = Color.White,
    secondaryContainer = LapislazuliClaro,
    onSecondaryContainer = LapislazuliProfundo,

    tertiary = Ocre,
    onTertiary = Color.White,
    tertiaryContainer = OcreClaro,
    onTertiaryContainer = OcreProfundo,

    background = Lino,
    onBackground = GredaNegra,
    surface = Lino,
    onSurface = GredaNegra,
    surfaceVariant = ArcillaClara,
    onSurfaceVariant = GredaMedia,
    surfaceTint = Cobre,
    inverseSurface = GredaInversa,
    inverseOnSurface = Lino,

    // Material 3 pinta NavigationBar con surfaceContainer y los campos y
    // tarjetas rellenas con surfaceContainerHighest: los tres quedan en
    // arcilla clara, como en los mockups.
    surfaceBright = LinoBrillante,
    surfaceDim = ArcillaMedia,
    surfaceContainerLowest = LinoBrillante,
    surfaceContainerLow = ArcillaSuave,
    surfaceContainer = ArcillaClara,
    surfaceContainerHigh = ArcillaClara,
    surfaceContainerHighest = ArcillaClara,

    outline = Borde,
    outlineVariant = BordeSuave,
    scrim = Color.Black,

    error = ErrorRojo,
    onError = Color.White,
    errorContainer = ErrorClaro,
    onErrorContainer = ErrorProfundo,
)
