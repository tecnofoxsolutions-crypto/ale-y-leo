package com.aleyleo.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Esquinas del concepto (sección 5.1). Los botones principales, los chips de
// categoría y el botón flotante usan CircleShape (píldora), no esta escala.
val AleYLeoFormas = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),       // campos de texto, chips de estado
    medium = RoundedCornerShape(12.dp),     // tarjetas de artesanía, fotos, diálogos
    large = RoundedCornerShape(20.dp),      // hoja inferior del Detalle
    extraLarge = RoundedCornerShape(28.dp),
)

// Hoja del Detalle: solo las esquinas superiores redondeadas.
val FormaHoja = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
