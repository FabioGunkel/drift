package com.gunkel.android.affectus.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

object Affectus {
    val colors: Colors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val dimens: Dimens
        @Composable
        @ReadOnlyComposable
        get() = LocalDimens.current

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}

@Composable
fun Affectus(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LightColors else LightColors
    
    CompositionLocalProvider(
        LocalColors provides colorScheme,
        LocalDimens provides Dimens(),
        LocalTypography provides Typography()
    ) {
        content()
    }
}
