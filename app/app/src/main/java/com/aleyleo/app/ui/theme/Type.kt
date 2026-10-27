package com.aleyleo.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.aleyleo.app.R

// Fraunces (títulos) y Work Sans (texto e interfaz), ambas fuentes variables
// en res/font. Fraunces trae por defecto peso 900 y formas irregulares (WONK),
// así que cada peso fija sus ejes de forma explícita:
//   SOFT 100 -> remates redondeados, como letra tallada
//   WONK 0   -> sin formas irregulares
//   opsz 72  -> diseño para tamaño de título
// En Android 7 (API 24-25) los ejes se ignoran y se ve el peso por defecto.

@OptIn(ExperimentalTextApi::class)
private fun fraunces(peso: Int) = Font(
    resId = R.font.fraunces,
    weight = FontWeight(peso),
    variationSettings = FontVariation.Settings(
        FontVariation.weight(peso),
        FontVariation.Setting("SOFT", 100f),
        FontVariation.Setting("WONK", 0f),
        FontVariation.Setting("opsz", 72f),
    ),
)

@OptIn(ExperimentalTextApi::class)
private fun workSans(peso: Int) = Font(
    resId = R.font.work_sans,
    weight = FontWeight(peso),
    variationSettings = FontVariation.Settings(FontVariation.weight(peso)),
)

val Fraunces = FontFamily(fraunces(400), fraunces(600))
val WorkSans = FontFamily(workSans(400), workSans(500), workSans(600))

// Escala del concepto (sección 4.1). Los roles que el concepto no usa
// mantienen las medidas de Material 3 con la familia que les corresponde.
val AleYLeoTipografia = Typography(
    displayLarge = TextStyle(fontFamily = Fraunces, fontWeight = FontWeight.SemiBold, fontSize = 57.sp, lineHeight = 64.sp),
    displayMedium = TextStyle(fontFamily = Fraunces, fontWeight = FontWeight.SemiBold, fontSize = 45.sp, lineHeight = 52.sp),
    displaySmall = TextStyle(fontFamily = Fraunces, fontWeight = FontWeight.SemiBold, fontSize = 36.sp, lineHeight = 44.sp),

    headlineLarge = TextStyle(fontFamily = Fraunces, fontWeight = FontWeight.SemiBold, fontSize = 32.sp, lineHeight = 40.sp),
    // Nombre de la pieza en el Detalle, total del carro, «El taller hoy».
    headlineMedium = TextStyle(fontFamily = Fraunces, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 36.sp),
    headlineSmall = TextStyle(fontFamily = Fraunces, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp),

    // Título de cada pantalla.
    titleLarge = TextStyle(fontFamily = Fraunces, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    // Nombre de la pieza en la tarjeta, títulos de sección, precio en la tarjeta.
    titleMedium = TextStyle(fontFamily = WorkSans, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall = TextStyle(fontFamily = WorkSans, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),

    // Descripción de la pieza.
    bodyLarge = TextStyle(fontFamily = WorkSans, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    // Texto secundario, datos del pedido.
    bodyMedium = TextStyle(fontFamily = WorkSans, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = WorkSans, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),

    // Botones.
    labelLarge = TextStyle(fontFamily = WorkSans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.01.em),
    // Chips, etiquetas de la barra inferior.
    labelMedium = TextStyle(fontFamily = WorkSans, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
    // Chip de estado y categoría en mayúsculas (aplicar uppercase() al texto).
    labelSmall = TextStyle(fontFamily = WorkSans, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.06.em),
)
