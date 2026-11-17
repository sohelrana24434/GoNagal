package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = GoNagalGreen,
    onPrimary = Color.White,
    primaryContainer = GoNagalGreenContainer,
    onPrimaryContainer = GoNagalOnGreenContainer,
    inversePrimary = GoNagalGreenLight,
    secondary = GoNagalNavy,
    onSecondary = Color.White,
    secondaryContainer = GoNagalSurfaceVariant,
    onSecondaryContainer = GoNagalNavy,
    tertiary = GoNagalOrange,
    onTertiary = Color.White,
    tertiaryContainer = GoNagalOrangeLight,
    onTertiaryContainer = GoNagalOrange,
    background = GoNagalBackground,
    onBackground = GoNagalTextPrimary,
    surface = GoNagalSurface,
    onSurface = GoNagalTextPrimary,
    surfaceVariant = GoNagalSurfaceVariant,
    onSurfaceVariant = GoNagalTextSecondary,
    outline = GoNagalBorder,
    outlineVariant = GoNagalBorderSubtle,
    error = GoNagalRed,
    onError = Color.White,
    errorContainer = GoNagalRedLight,
    onErrorContainer = GoNagalRed
)

private val DarkColorScheme = darkColorScheme(
    primary = GoNagalGreenLight,
    onPrimary = Color.Black,
    primaryContainer = GoNagalGreenDark,
    onPrimaryContainer = Color.White,
    secondary = Color.White,
    onSecondary = GoNagalNavy,
    secondaryContainer = GoNagalNavyLight,
    onSecondaryContainer = Color.White,
    tertiary = GoNagalOrange,
    onTertiary = Color.White,
    background = GoNagalNavyDark,
    onBackground = Color.White,
    surface = GoNagalNavySurface,
    onSurface = Color.White,
    surfaceVariant = GoNagalNavyLight,
    onSurfaceVariant = GoNagalTextMuted,
    outline = GoNagalNavyLight,
    error = GoNagalRed,
    onError = Color.White
)

@Composable
fun GoNagalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We enforce the professional GoNagal brand palette rather than system dynamic wallpaper
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

// Alias for backwards compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    GoNagalTheme(darkTheme = darkTheme, content = content)
}
