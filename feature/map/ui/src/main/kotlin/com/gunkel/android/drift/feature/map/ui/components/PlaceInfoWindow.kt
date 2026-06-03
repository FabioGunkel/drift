package com.gunkel.android.drift.feature.map.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun PlaceInfoWindow(
    place: Place,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = place.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            maxLines = 2
        )
        
        Text(
            text = stringResource(id = mapPlaceTypeToStringRes(place.type)),
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (place.photo != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
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
                    // Note: We're omitting the "forced refresh" logic here because
                    // DriftRepository now fetches bitmaps directly in the background.
                    // If we ever use URIs again, we'll need a non-looping refresh.
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
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                modifier = Modifier.padding(top = 12.dp),
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
