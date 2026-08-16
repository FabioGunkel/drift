package com.gunkel.android.affectus.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.gunkel.android.affectus.theme.Affectus

@Composable
fun DriftInfoWindow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val backgroundColor = Affectus.colors.background
    val borderColor = Affectus.colors.secondary.copy(alpha = 0.3f)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(280.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(Affectus.dimens.borderThin, borderColor, RoundedCornerShape(Affectus.dimens.radiusXL))
                .clip(RoundedCornerShape(Affectus.dimens.radiusXL))
                .background(backgroundColor)
                .padding(Affectus.dimens.spacingM),
            horizontalAlignment = Alignment.Start
        ) {
            content()
        }
        
        // The "V" anchor
        Canvas(
            modifier = Modifier
                .size(Affectus.dimens.radiusXL, Affectus.dimens.radiusM)
                .offset(y = (-1).dp) // Overlap to make it seamless
        ) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width / 2f, size.height)
                lineTo(size.width, 0f)
                close()
            }
            drawPath(path, color = backgroundColor)
            
            // Draw border on the V sides (not the top)
            val strokePath = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width / 2f, size.height)
                lineTo(size.width, 0f)
            }
            drawPath(
                path = strokePath,
                color = borderColor,
                style = Stroke(width = 2f)
            )
        }
    }
}
