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
import com.aleyleo.app.R
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import com.aleyleo.app.ui.theme.Lino
import com.aleyleo.app.view.componentes.VersionLogo

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

    // La "mesa del taller" de fondo y el logo en lino encima (así lo indica la guía del logo).
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.mesa_vertical),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LogoAleYLeo(version = VersionLogo.VERTICAL_LINO, modifier = Modifier.width(220.dp))
            Text(
                text = "Artesanía chilena",
                style = MaterialTheme.typography.titleMedium,
                color = Lino,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}
