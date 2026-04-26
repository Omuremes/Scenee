package com.example.cinescope.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Blush,
    onPrimary = Ink,
    secondary = SurfaceMuted,
    onSecondary = SurfaceHigh,
    tertiary = Warning,
    background = Ink,
    onBackground = Surface,
    surface = Color(0xFF202328),
    onSurface = Surface,
    surfaceVariant = Color(0xFF2A2E34),
    onSurfaceVariant = Color(0xFFBCC2CA),
    outline = Color(0xFF3A4048)
)

private val LightColorScheme = lightColorScheme(
    primary = Crimson,
    onPrimary = SurfaceHigh,
    secondary = SurfaceMuted,
    onSecondary = Ink,
    tertiary = Warning,
    background = Surface,
    onBackground = Ink,
    surface = SurfaceHigh,
    onSurface = Ink,
    surfaceVariant = SurfaceMuted,
    onSurfaceVariant = Slate,
    outline = Outline
)

@Composable
fun CineScopeTheme(
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
