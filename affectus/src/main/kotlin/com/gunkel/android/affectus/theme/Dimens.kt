package com.gunkel.android.affectus.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Dimens(
    // Grid & Spacing
    val spacingNone: Dp = 0.dp,
    val spacingXXS: Dp = 2.dp,
    val spacingXS: Dp = 4.dp,
    val spacingS: Dp = 8.dp,
    val spacingM: Dp = 16.dp,
    val spacingL: Dp = 24.dp,
    val spacingXL: Dp = 32.dp,
    val spacingXXL: Dp = 48.dp,
    val spacingXXXL: Dp = 64.dp,

    // Border Widths
    val borderNone: Dp = 0.dp,
    val borderThin: Dp = 1.dp,
    val borderMedium: Dp = 1.5.dp,
    val borderThick: Dp = 2.dp,

    // Corner Radii
    val radiusNone: Dp = 0.dp,
    val radiusXS: Dp = 4.dp,
    val radiusS: Dp = 8.dp,
    val radiusM: Dp = 12.dp,
    val radiusL: Dp = 16.dp,
    val radiusXL: Dp = 24.dp,
    val radiusFull: Dp = 1000.dp, // For circular elements

    // Icon Sizes
    val iconXS: Dp = 16.dp,
    val iconS: Dp = 24.dp,
    val iconM: Dp = 32.dp,
    val iconL: Dp = 48.dp,
    val iconXL: Dp = 64.dp,

    // Button Sizes
    val buttonHeightSmall: Dp = 32.dp,
    val buttonHeightMedium: Dp = 44.dp,
    val buttonHeightLarge: Dp = 56.dp
)

val LocalDimens = staticCompositionLocalOf { Dimens() }
