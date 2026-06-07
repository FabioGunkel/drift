package com.gunkel.android.drift.feature.map.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
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
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "morphFactor"
    )

    // Dash animation for drawing effect
    val dashPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dashPhase"
    )

    Box(
        modifier = modifier
            .widthIn(min = 140.dp) // Smaller button
            .height(48.dp)
            // Outer Gold Stroke
            .border(2.dp, AffectusTheme.colors.primary, RoundedCornerShape(24.dp))
            .padding(2.dp)
            // Inner Off-white Stroke
            .border(1.5.dp, AffectusTheme.colors.background, RoundedCornerShape(22.dp))
            .padding(1.5.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(AffectusTheme.colors.primary)
            .clickable(enabled = !isLoading) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Text Layer
        Text(
            text = stringResource(id = CoreR.string.drift_button_label),
            style = AffectusTheme.typography.titleMedium,
            color = AffectusTheme.colors.background,
            modifier = Modifier.graphicsLayer {
                scaleY = textScaleY
                alpha = if (textScaleY > 0.1f) 1f else 0f
            }
        )

        // Path Animation Layer (Dashed "Drift" path)
        if (pathAlpha > 0.01f) {
            val pathColor = AffectusTheme.colors.background
            Canvas(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(24.dp)
                    .graphicsLayer { alpha = pathAlpha }
            ) {
                val width = size.width
                val height = size.height
                val centerY = height / 2f

                val path = Path().apply {
                    moveTo(0f, centerY)
                    
                    // Psychogeographic path curves (inspired by references)
                    val p1y = centerY + (morphFactor * 12f - 6f)
                    val p2y = centerY - (morphFactor * 18f - 9f)
                    val p3y = centerY + (morphFactor * 8f - 4f)

                    cubicTo(
                        width * 0.2f, p1y,
                        width * 0.5f, p2y,
                        width * 0.7f, p3y
                    )
                    lineTo(width, centerY)
                }

                drawPath(
                    path = path,
                    color = pathColor,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(10f, 10f),
                            phase = dashPhase
                        )
                    )
                )
            }
        }
    }
}
