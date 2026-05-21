package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    secondary = NeonCyan,
    tertiary = NeonPink,
    background = DeepSlateBg,
    surface = CardBg,
    onPrimary = Color.White,
    onSecondary = DeepSlateBg,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = BorderColor,
    primaryContainer = LightAccent,
    secondaryContainer = SliderInactive,
    error = NeonPink
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme default
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
