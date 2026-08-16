package com.gunkel.android.drift.feature.map.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.bitmapConfig
import coil3.request.crossfade
import com.gunkel.android.drift.feature.map.data.models.Place
import com.gunkel.android.drift.feature.map.data.models.PlaceType
import com.gunkel.android.drift.core.ui.R
import com.gunkel.android.affectus.theme.AffectusTheme

@Composable
fun PlaceInfoWindow(
    place: Place,
    modifier: Modifier = Modifier
) {
    val backgroundColor = AffectusTheme.colors.background
    val borderColor = AffectusTheme.colors.secondary.copy(alpha = 0.3f)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(280.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(AffectusTheme.dimens.borderThin, borderColor, RoundedCornerShape(AffectusTheme.dimens.radiusXL))
                .clip(RoundedCornerShape(AffectusTheme.dimens.radiusXL))
                .background(backgroundColor)
                .padding(AffectusTheme.dimens.spacingM),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = place.name,
                style = AffectusTheme.typography.titleMedium,
                color = AffectusTheme.colors.onBackground,
                maxLines = 2
            )
            
            Text(
                text = stringResource(id = mapPlaceTypeToStringRes(place.type)),
                style = AffectusTheme.typography.labelSmall,
                color = AffectusTheme.colors.secondary.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = AffectusTheme.dimens.spacingM)
            )

            if (place.photo != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(AffectusTheme.dimens.radiusM))
                        .background(AffectusTheme.colors.secondary.copy(alpha = 0.1f))
                ) {
                    val data = place.photo
                    if (data is Bitmap) {
                        Image(
                            painter = remember(data) { BitmapPainter(data.asImageBitmap()) },
                            contentDescription = place.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(data)
                                    .crossfade(true)
                                    .allowHardware(false)
                                    .bitmapConfig(Bitmap.Config.ARGB_8888)
                                    .build()
                            ),
                            contentDescription = place.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            val description = place.description
            if (!description.isNullOrBlank()) {
                Text(
                    text = description,
                    style = AffectusTheme.typography.bodyMedium,
                    color = AffectusTheme.colors.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = AffectusTheme.dimens.spacingM),
                    lineHeight = 20.sp
                )
            }
        }
        
        // The "V" anchor
        Canvas(
            modifier = Modifier
                .size(AffectusTheme.dimens.radiusXL, AffectusTheme.dimens.radiusM)
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

private fun mapPlaceTypeToStringRes(type: PlaceType): Int {
    return when (type) {
        PlaceType.MUSEUM -> R.string.place_type_museum
        PlaceType.PARK -> R.string.place_type_park
        PlaceType.TOURIST_ATTRACTION -> R.string.place_type_tourist_attraction
        PlaceType.HISTORIC_SITE -> R.string.place_type_historic_site
        PlaceType.RESTAURANT -> R.string.place_type_restaurant
        PlaceType.STORE -> R.string.place_type_store
        PlaceType.OTHER -> R.string.place_type_other
    }
}
