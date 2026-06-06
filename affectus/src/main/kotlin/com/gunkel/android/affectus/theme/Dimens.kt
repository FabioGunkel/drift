package com.gunkel.android.affectus.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AffectusDimens(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val iconSmall: Dp = 24.dp,
    val iconMedium: Dp = 32.dp,
    val iconLarge: Dp = 48.dp
)

val LocalAffectusDimens = staticCompositionLocalOf { AffectusDimens() }
