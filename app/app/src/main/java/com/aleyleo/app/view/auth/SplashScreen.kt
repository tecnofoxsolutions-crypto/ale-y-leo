package com.aleyleo.app.view.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

// P01 Splash: muestra la marca y después de 1,5 s pasa al login.
// (AP-06 agregará el logo y la revisión de sesión.)
@Composable
fun SplashScreen(onTerminado: () -> Unit) {

    // Se ejecuta una sola vez cuando la pantalla aparece.
    LaunchedEffect(Unit) {
        delay(1500)
        onTerminado()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ale y Leo",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = "Artesanía chilena")
    }
}
