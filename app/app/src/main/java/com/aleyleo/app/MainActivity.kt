package com.aleyleo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aleyleo.app.navigation.AppNavHost
import com.aleyleo.app.ui.theme.AleYLeoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AleYLeoTheme {
                AppNavHost()
            }
        }
    }
}
