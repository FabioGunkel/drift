package com.gunkel.android.drift.feature.map.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gunkel.android.affectus.theme.AffectusTheme
import com.gunkel.android.drift.core.ui.R as CoreR

@Composable
fun DriftButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation for text vertical scale
    val textScaleY by animateFloatAsState(
        targetValue = if (isLoading) 0f else 1f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "textScale"
    )

    // Animation for path visibility/line appearance
    val pathAlpha by animateFloatAsState(
        targetValue = if (isLoading) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "pathAlpha"
    )

    // Infinite animation for path morphing
    val infiniteTransition = rememberInfiniteTransition(label = "pathDrift")
    val morphFactor by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "morphFactor"
    )

    Surface(
        modifier = modifier
            .height(56.dp)
            .widthIn(min = 160.dp)
            .clip(RoundedCornerShape(28.dp))
            .clickable(enabled = !isLoading) { onClick() },
        color = AffectusTheme.colors.primary,
        contentColor = AffectusTheme.colors.background // TheVoidWhite
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // Text Layer
            Text(
                text = stringResource(id = CoreR.string.drift_button_label),
                style = AffectusTheme.typography.titleMedium,
                modifier = Modifier.graphicsLayer {
                    scaleY = textScaleY
                    alpha = if (textScaleY > 0.1f) 1f else 0f
                }
            )

            // Path Animation Layer
            if (pathAlpha > 0.01f) {
                val pathColor = AffectusTheme.colors.background
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(20.dp)
                        .graphicsLayer { alpha = pathAlpha }
                ) {
                    val width = size.width
                    val height = size.height
                    val centerY = height / 2f

                    val path = Path().apply {
                        moveTo(0f, centerY)
                        
                        // Create 2 dynamic points for the "drift" path
                        val p1y = centerY + (morphFactor * 15f - 7.5f)
                        val p2y = centerY - (morphFactor * 20f - 10f)

                        quadraticTo(width * 0.25f, p1y, width * 0.5f, centerY)
                        quadraticTo(width * 0.75f, p2y, width, centerY)
                    }

                    drawPath(
                        path = path,
                        color = pathColor,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }
    }
}
