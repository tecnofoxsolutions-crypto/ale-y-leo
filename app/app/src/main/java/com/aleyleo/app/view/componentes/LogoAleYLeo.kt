package com.aleyleo.app.view.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aleyleo.app.ui.theme.AleYLeoTheme

// Logo provisorio: un círculo de color con un ícono.
// Se reemplaza por el logo de Emanuel cuando esté listo (tarjeta DI-02).
@Composable
fun LogoAleYLeo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Diamond,
            contentDescription = "Logo de Ale y Leo",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        )
    }
}

@Preview
@Composable
fun LogoAleYLeoPreview() {
    AleYLeoTheme { LogoAleYLeo(modifier = Modifier.size(120.dp)) }
}
