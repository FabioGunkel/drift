package com.gunkel.android.affectus.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.gunkel.android.affectus.theme.Affectus

@Composable
fun DriftToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 24.dp else 0.dp,
        label = "thumbOffset"
    )

    Box(
        modifier = modifier
            .width(52.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(Affectus.dimens.radiusXL))
            .background(if (checked) Affectus.colors.primary else Affectus.colors.secondary.copy(alpha = 0.2f))
            .border(
                width = Affectus.dimens.borderThin,
                color = if (checked) Affectus.colors.primary else Affectus.colors.secondary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(Affectus.dimens.radiusXL)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCheckedChange(!checked) }
            )
            .padding(4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(20.dp)
                .clip(CircleShape)
                .background(Affectus.colors.background)
                .border(
                    width = Affectus.dimens.borderThin,
                    color = if (checked) Affectus.colors.primary else Affectus.colors.secondary.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        )
    }
}
