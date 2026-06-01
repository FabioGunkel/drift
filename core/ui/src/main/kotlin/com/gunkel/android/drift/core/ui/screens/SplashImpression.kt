package com.gunkel.android.drift.core.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gunkel.android.drift.core.ui.theme.DriftTeal
import com.gunkel.android.drift.core.ui.theme.HistoryAmber

@Composable
fun SplashImpression(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DriftTeal),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "DRIFT",
                color = HistoryAmber,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                letterSpacing = 8.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Discover the safe way",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 2.sp
            )
        }
    }
}

@Preview
@Composable
fun SplashImpressionPreview() {
    SplashImpression()
}
