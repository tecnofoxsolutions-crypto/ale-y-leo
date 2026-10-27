package com.aleyleo.app.view.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aleyleo.app.R
import com.aleyleo.app.ui.theme.AleYLeoTheme

// Versiones del logo de Emanuel (DI-02, carpeta 04_logo):
// horizontal = uso principal; vertical = espacios cuadrados (Splash);
// lino = sobre la foto de la mesa o fondos oscuros; símbolo = tamaños chicos.
enum class VersionLogo(val recurso: Int) {
    HORIZONTAL(R.drawable.logo_horizontal),
    VERTICAL(R.drawable.logo_vertical),
    VERTICAL_LINO(R.drawable.logo_vertical_lino),
    SIMBOLO(R.drawable.simbolo)
}

@Composable
fun LogoAleYLeo(
    modifier: Modifier = Modifier,
    version: VersionLogo = VersionLogo.VERTICAL
) {
    Image(
        painter = painterResource(version.recurso),
        contentDescription = "Logo de Ale y Leo",
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun LogoAleYLeoPreview() {
    AleYLeoTheme { LogoAleYLeo(modifier = Modifier.width(240.dp), version = VersionLogo.HORIZONTAL) }
}
