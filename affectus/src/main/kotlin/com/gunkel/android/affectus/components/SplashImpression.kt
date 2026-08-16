package com.gunkel.android.affectus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gunkel.android.affectus.theme.Affectus
import com.gunkel.android.affectus.R

@Composable
fun SplashImpression(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Affectus.colors.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.app_display_name),
                color = Affectus.colors.secondary,
                style = Affectus.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(Affectus.dimens.spacingM))
            Text(
                text = stringResource(id = R.string.app_slogan),
                color = Affectus.colors.onPrimary.copy(alpha = 0.7f),
                style = Affectus.typography.bodyMedium
            )
        }
    }
}

@Preview
@Composable
fun SplashImpressionPreview() {
    Affectus {
        SplashImpression()
    }
}
