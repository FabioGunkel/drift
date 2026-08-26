package com.gunkel.android.drift.feature.map.ui.components

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.bitmapConfig
import coil3.request.crossfade
import coil3.toBitmap
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FetchResolvedPhotoUriRequest
import com.gunkel.android.affectus.theme.Affectus
import com.gunkel.android.drift.feature.map.data.models.Place
import com.gunkel.android.drift.feature.map.data.models.PlaceType
import com.gunkel.android.drift.core.ui.R
import kotlinx.coroutines.tasks.await

@Composable
fun PlaceInfoWindowContent(
    place: Place,
    modifier: Modifier = Modifier,
    isClickable: Boolean = false,
    showAddIcon: Boolean = true,
    showAiSummary: Boolean = false,
    onAddClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val placesClient = remember(context) { Places.createClient(context) }
    val imageLoader = remember(context) { ImageLoader(context) }
    var photoBitmap by remember(place.id) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(place.photoMetadata) {
        val metadata = place.photoMetadata
        if (metadata != null) {
            try {
                val uriRequest = FetchResolvedPhotoUriRequest.builder(metadata)
                    .setMaxWidth(800)
                    .setMaxHeight(800)
                    .build()
                val uriResponse = placesClient.fetchResolvedPhotoUri(uriRequest).await()
                val uri = uriResponse.uri

                if (uri != null) {
                    val coilRequest = ImageRequest.Builder(context)
                        .data(uri.toString())
                        .allowHardware(false)
                        .bitmapConfig(Bitmap.Config.ARGB_8888)
                        .size(400, 400)
                        .build()

                    val result = imageLoader.execute(coilRequest)
                    val bitmap = result.image?.toBitmap()
                    
                    if (bitmap != null) {
                        photoBitmap = if (bitmap.config == Bitmap.Config.HARDWARE) {
                            bitmap.copy(Bitmap.Config.ARGB_8888, false)
                        } else {
                            bitmap
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("PlaceInfoWindow", "[${place.name}] Photo failed: ${e.message}")
            }
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
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
            }
            
            if (showAddIcon) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Affectus.colors.primary.copy(alpha = 0.1f))
                        .then(
                            if (isClickable && onAddClick != null) {
                                Modifier.clickable { onAddClick() }
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Ver detalhes",
                        tint = Affectus.colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(Affectus.dimens.radiusM))
                .background(Affectus.colors.secondary.copy(alpha = 0.05f)),
            contentAlignment = Alignment.Center
        ) {
            if (photoBitmap != null) {
                Image(
                    painter = remember(photoBitmap) { BitmapPainter(photoBitmap!!.asImageBitmap()) },
                    contentDescription = place.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Affectus.colors.onBackground.copy(alpha = 0.2f)
                )
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

        val aiSummary = place.aiSummary
        if (showAiSummary && !aiSummary.isNullOrBlank()) {
            var isExpanded by remember { mutableStateOf(false) }
            
            Spacer(modifier = Modifier.height(Affectus.dimens.spacingM))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Affectus.colors.primary.copy(alpha = 0.05f)
                ),
                shape = RoundedCornerShape(Affectus.dimens.radiusS),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
            ) {
                Row(
                    modifier = Modifier.padding(Affectus.dimens.spacingS),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Resumo IA",
                        tint = Affectus.colors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(Affectus.dimens.spacingS))
                    Text(
                        text = aiSummary,
                        style = Affectus.typography.labelSmall,
                        color = Affectus.colors.primary,
                        lineHeight = 16.sp,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                        overflow = if (isExpanded) TextOverflow.Clip else TextOverflow.Ellipsis
                    )
                }
            }
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
