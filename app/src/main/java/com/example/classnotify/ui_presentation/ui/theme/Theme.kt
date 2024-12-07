package com.example.classnotify.ui_presentation.ui.theme

import androidx.compose.ui.graphics.Color
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Colores personalizados
val RedColor = Color(0xFFD32F2F) // Rojo
val WhiteColor = Color(0xFFFFFFFF) // Blanco
val BlackColor = Color(0xFF000000) // Negro

// Esquema de colores oscuro
private val DarkColorScheme = darkColorScheme(
    primary = RedColor,  // Rojo para el color primario
    secondary = RedColor,  // Rojo para el color secundario
    background = WhiteColor, // Fondo blanco en el tema oscuro
    surface = WhiteColor, // Superficie blanca en el tema oscuro
    onPrimary = WhiteColor, // Texto blanco sobre el color primario
    onSecondary = WhiteColor, // Texto blanco sobre el color secundario
    onBackground = BlackColor, // Texto negro sobre el fondo blanco
    onSurface = BlackColor// Texto negro sobre la superficie blanca
)

// Esquema de colores claro
private val LightColorScheme = lightColorScheme(
    primary = RedColor, // Rojo como color primario
    secondary = RedColor, // Rojo como color secundario
    background = WhiteColor, // Fondo blanco en el tema claro
    surface = WhiteColor, // Superficie blanca en el tema claro
    onPrimary = WhiteColor, // Texto blanco sobre el color primario
    onSecondary = WhiteColor, // Texto blanco sobre el color secundario
    onBackground = BlackColor, // Texto negro sobre fondo blanco
    onSurface = BlackColor // Texto negro sobre la superficie blanca
)
@Composable
fun ClassNotifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

