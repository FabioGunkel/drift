package com.gunkel.android.affectus.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gunkel.android.affectus.theme.Affectus
import com.gunkel.android.affectus.R

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

    // Dash animation for "walking" footsteps effect
    val infiniteTransition = rememberInfiniteTransition(label = "pathDrift")
    val footstepPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "footstepPhase"
    )

    Box(
        modifier = modifier
            .width(110.dp) // Even smaller button
            .height(Affectus.dimens.buttonHeightMedium)
            // Outer Gold Stroke
            .border(
                Affectus.dimens.borderThick, 
                Affectus.colors.primary, 
                RoundedCornerShape(Affectus.dimens.radiusXL)
            )
            .padding(Affectus.dimens.borderThick)
            // Inner Off-white Stroke
            .border(
                Affectus.dimens.borderMedium, 
                Affectus.colors.background, 
                RoundedCornerShape(Affectus.dimens.radiusXL - Affectus.dimens.borderThick)
            )
            .padding(Affectus.dimens.borderMedium)
            .clip(RoundedCornerShape(Affectus.dimens.radiusL))
            .background(Affectus.colors.primary)
            .clickable(enabled = !isLoading) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Text Layer
        Text(
            text = stringResource(id = R.string.drift_button_label),
            style = Affectus.typography.titleMedium,
            color = Affectus.colors.background,
            modifier = Modifier.graphicsLayer {
                scaleY = textScaleY
                alpha = if (textScaleY > 0.1f) 1f else 0f
            }
        )

        // Path Animation Layer (Footsteps)
        if (pathAlpha > 0.01f) {
            val pathColor = Affectus.colors.background
            Canvas(
                modifier = Modifier
                    .fillMaxWidth(0.7f) // Matches approximate text length
                    .height(Affectus.dimens.iconS)
                    .graphicsLayer { alpha = pathAlpha }
            ) {
                val width = size.width
                val height = size.height
                val centerY = height / 2f

                // Zig-zag base path
                val path = Path().apply {
                    moveTo(0f, centerY)
                    val segments = 4
                    for (i in 1..segments) {
                        val x = (width / segments) * i
                        // Static zig-zag offset
                        val yOffset = if (i % 2 == 0) 6f else -6f
                        lineTo(x, centerY + yOffset)
                    }
                }

                // Footsteps: Two parallel lines with offset dashes
                val footSep = 3.dp.toPx()
                val dashIntervals = floatArrayOf(6f, 34f) // Shorter "feet", longer gap

                // Left Foot (offset above the path line)
                withTransform({ translate(top = -footSep) }) {
                    drawPath(
                        path = path,
                        color = pathColor,
                        style = Stroke(
                            width = 4.dp.toPx(), // Wider for "boot" look
                            cap = StrokeCap.Round,
                            pathEffect = PathEffect.dashPathEffect(
                                intervals = dashIntervals,
                                phase = footstepPhase
                            )
                        )
                    )
                }

                // Right Foot (offset below the path line)
                withTransform({ translate(top = footSep) }) {
                    drawPath(
                        path = path,
                        color = pathColor,
                        style = Stroke(
                            width = 4.dp.toPx(),
                            cap = StrokeCap.Round,
                            pathEffect = PathEffect.dashPathEffect(
                                intervals = dashIntervals,
                                phase = footstepPhase + 20f // Out of phase
                            )
                        )
                    )
                }
            }
        }
    }
}
