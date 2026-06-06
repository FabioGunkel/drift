package com.gunkel.android.affectus.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// The Encounter Palette (Psychogeographic)
val EncounterGold = Color(0xFFD4AF37)      // Antique Gold (Discovery)
val AtmosphereMidnight = Color(0xFF2C3E50) // Midnight Blue (Depth)
val TensionCrimson = Color(0xFFE74C3C)     // Alizarin Crimson (Emotion)
val TheVoidWhite = Color(0xFFECF0F1)       // Off-white (Canvas)
val DeepCarbon = Color(0xFF1A1A1A)         // Text/Contrast

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
    primary = EncounterGold,
    secondary = AtmosphereMidnight,
    tertiary = TensionCrimson,
    background = TheVoidWhite,
    surface = Color.White,
    error = TensionCrimson,
    onPrimary = DeepCarbon,
    onSecondary = Color.White,
    onBackground = DeepCarbon,
    onSurface = DeepCarbon,
    onError = Color.White
)

val LocalColors = staticCompositionLocalOf { LightColors }
