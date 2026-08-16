package com.gunkel.android.drift.core.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gunkel.android.affectus.theme.AffectusTheme
import com.gunkel.android.drift.core.ui.R

@Composable
fun SplashImpression(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AffectusTheme.colors.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.app_display_name),
                color = AffectusTheme.colors.secondary,
                style = AffectusTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(AffectusTheme.dimens.spacingM))
            Text(
                text = stringResource(id = R.string.app_slogan),
                color = AffectusTheme.colors.onPrimary.copy(alpha = 0.7f),
                style = AffectusTheme.typography.bodyMedium
            )
        }
    }
}

@Preview
@Composable
fun SplashImpressionPreview() {
    AffectusTheme {
        SplashImpression()
    }
}
