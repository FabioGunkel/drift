package com.gunkel.android.drift.feature.map.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
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
import com.gunkel.android.affectus.theme.Affectus
import com.gunkel.android.drift.feature.map.data.models.Place
import com.gunkel.android.drift.feature.map.data.models.PlaceType
import com.gunkel.android.drift.core.ui.R

@Composable
fun PlaceInfoWindowContent(
    place: Place,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = place.name,
            style = Affectus.typography.titleMedium,
            color = Affectus.colors.onBackground,
            maxLines = 2
        )
        
        Text(
            text = stringResource(id = mapPlaceTypeToStringRes(place.type)),
            style = Affectus.typography.labelSmall,
            color = Affectus.colors.secondary.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = Affectus.dimens.spacingM)
        )

        if (place.photo != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(Affectus.dimens.radiusM))
                    .background(Affectus.colors.secondary.copy(alpha = 0.1f))
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
                style = Affectus.typography.bodyMedium,
                color = Affectus.colors.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = Affectus.dimens.spacingM),
                lineHeight = 20.sp
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
