package com.gunkel.android.affectus.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val DriftTeal = Color(0xFF004D40)
val HistoryAmber = Color(0xFFFFC107)
val SafetyGreen = Color(0xFF2E7D32)
val PaperWhite = Color(0xFFF5F5F5)

@Immutable
data class Colors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val background: Color,
    val surface: Color,
    val error: Color,
    val onPrimary: Color,
    val onSecondary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val onError: Color
)

val LightColors = Colors(
    primary = DriftTeal,
    secondary = HistoryAmber,
    tertiary = SafetyGreen,
    background = PaperWhite,
    surface = Color.White,
    error = Color(0xFFB00020),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

val LocalColors = staticCompositionLocalOf { LightColors }
