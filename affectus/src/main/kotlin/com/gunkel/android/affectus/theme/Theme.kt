package com.gunkel.android.affectus.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

object AffectusTheme {
    val colors: AffectusColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAffectusColors.current

    val dimens: AffectusDimens
        @Composable
        @ReadOnlyComposable
        get() = LocalAffectusDimens.current

    val typography: AffectusTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalAffectusTypography.current
}

@Composable
fun AffectusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LightAffectusColors else LightAffectusColors
    
    CompositionLocalProvider(
        LocalAffectusColors provides colorScheme,
        LocalAffectusDimens provides AffectusDimens(),
        LocalAffectusTypography provides AffectusTypography()
    ) {
        content()
    }
}
