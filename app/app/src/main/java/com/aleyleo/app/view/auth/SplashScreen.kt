package com.aleyleo.app.view.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aleyleo.app.model.Rol
import com.aleyleo.app.view.componentes.LogoAleYLeo
import com.aleyleo.app.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

// P01 Splash: muestra la marca y decide a dónde ir.
// Con sesión iniciada entra directo al grafo de su rol; sin sesión va al Login (HU-02).
// En la entrega final la sesión se recupera del token guardado en DataStore.
@Composable
fun SplashScreen(
    onSinSesion: () -> Unit,
    onCliente: () -> Unit,
    onAdmin: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    // Se ejecuta una sola vez cuando la pantalla aparece.
    LaunchedEffect(Unit) {
        delay(1500)
        when (authViewModel.sesion.value?.rol) {
            Rol.CLIENTE -> onCliente()
            Rol.ADMIN -> onAdmin()
            null -> onSinSesion()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LogoAleYLeo(modifier = Modifier.size(120.dp))
        Text(
            text = "Ale y Leo",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = "Artesanía chilena")
    }
}
