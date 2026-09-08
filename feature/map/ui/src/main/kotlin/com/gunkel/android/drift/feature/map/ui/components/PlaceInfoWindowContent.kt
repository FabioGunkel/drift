package com.gunkel.android.drift.feature.map.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gunkel.android.affectus.theme.Affectus
import com.gunkel.android.drift.feature.map.data.models.Place
import com.gunkel.android.drift.feature.map.data.models.PlaceType
import com.gunkel.android.drift.core.ui.R

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

        // Placeholder Area (Replaces costly Photo API)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(Affectus.dimens.radiusM))
                .background(Affectus.colors.secondary.copy(alpha = 0.1f))
                .clickable {
                    // Deep link to Google Maps (Free for user and developer)
                    val uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(place.name)}&query_place_id=${place.id}")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    context.startActivity(intent)
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(Affectus.dimens.spacingL)
            ) {
                Icon(
                    imageVector = mapPlaceTypeToIcon(place.type),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = Affectus.colors.primary.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(Affectus.dimens.spacingM))
                Text(
                    text = "Ver fotos e avaliações no Google Maps",
                    style = Affectus.typography.labelSmall,
                    color = Affectus.colors.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "(Economize dados e bateria)",
                    style = Affectus.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Affectus.colors.onBackground.copy(alpha = 0.3f),
                    textAlign = TextAlign.Center
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

private fun mapPlaceTypeToIcon(type: PlaceType): ImageVector {
    return when (type) {
        PlaceType.MUSEUM -> Icons.Default.Museum
        PlaceType.PARK -> Icons.Default.Park
        PlaceType.TOURIST_ATTRACTION -> Icons.Default.Attractions
        PlaceType.HISTORIC_SITE -> Icons.Default.AccountBalance
        PlaceType.RESTAURANT -> Icons.Default.Restaurant
        PlaceType.STORE -> Icons.Default.Store
        PlaceType.OTHER -> Icons.Default.Place
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
