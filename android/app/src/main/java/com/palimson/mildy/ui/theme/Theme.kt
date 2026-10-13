package com.palimson.mildy.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MildyBrightRed,
    secondary = MildyRed,
    tertiary = MildyRose,
    background = MildyBgPrimary,
    surface = MildyBgCard,
    onPrimary = MildyTextPrimary,
    onSecondary = MildyTextPrimary,
    onBackground = MildyTextPrimary,
    onSurface = MildyTextPrimary,
    outline = MildyBorder
)

private val LightColorScheme = lightColorScheme(
    primary = MildyRed,
    secondary = MildyBurgundy,
    tertiary = MildyCrimson,
    background = MildyLightBgPrimary,
    surface = MildyLightBgCard,
    onPrimary = MildyTextPrimary,
    onSecondary = MildyTextPrimary,
    onBackground = MildyLightTextPrimary,
    onSurface = MildyLightTextPrimary,
    outline = MildyBorder
)

@Composable
fun MildyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MildyTypography,
        content = content
    )
}
