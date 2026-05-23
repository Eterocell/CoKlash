package com.github.kr328.clash.design.compose.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Extracted from existing AppThemeLight/AppThemeDark in themes.xml
private val ClashLightPrimary = Color(0xFF1E4376)
private val ClashDarkPrimary = Color(0xFF1976D2)
private val LightBackground = Color(0xFFFAFAFA)
private val DarkBackground = Color(0xFF121212)
private val DarkSurface = Color(0xFF202020)
private val ErrorColor = Color(0xFFB00020)

val LightColorScheme = lightColorScheme(
    primary = ClashLightPrimary,
    onPrimary = Color.White,
    background = LightBackground,
    surface = LightBackground,
    error = ErrorColor,
)

val DarkColorScheme = darkColorScheme(
    primary = ClashDarkPrimary,
    onPrimary = Color.White,
    background = DarkBackground,
    surface = DarkSurface,
    error = ErrorColor,
)
