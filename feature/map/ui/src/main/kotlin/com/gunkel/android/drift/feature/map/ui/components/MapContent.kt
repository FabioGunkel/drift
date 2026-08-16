package com.gunkel.android.drift.feature.map.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import com.gunkel.android.affectus.theme.Affectus
import com.gunkel.android.affectus.theme.MarkerUtils
import com.gunkel.android.drift.core.common.PolylineDecoder
import com.gunkel.android.drift.core.ui.R as CoreR
import com.gunkel.android.drift.core.ui.components.DriftButton
import com.gunkel.android.drift.core.ui.components.PlaceInfoWindow
import com.gunkel.android.drift.feature.map.ui.viewmodels.DriftUiState

@SuppressLint("MissingPermission")
@Composable
fun MapContent(
    uiState: DriftUiState,
    onDriftClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-23.5616, -46.6866), 15f)
    }
    
    val mapStyleOptions = remember(context) {
        MapStyleOptions.loadRawResourceStyle(context, CoreR.raw.map_style)
    }

    // Auto-zoom to path when found
    LaunchedEffect(uiState) {
        if (uiState is DriftUiState.PathFound && uiState.stops.isNotEmpty()) {
            val builder = LatLngBounds.builder()
            uiState.stops.forEach { 
                builder.include(LatLng(it.location.latitude, it.location.longitude)) 
            }
            val bounds = builder.build()
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngBounds(bounds, 200),
                durationMs = 1000
            )
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .semantics { testTagsAsResourceId = true },
        floatingActionButton = {
            DriftButton(
                isLoading = uiState is DriftUiState.Loading,
                onClick = {
                    val projection = cameraPositionState.projection
                    val radius = if (projection != null) {
                        val visibleRegion = projection.visibleRegion
                        val center = cameraPositionState.position.target
                        val corner = visibleRegion.farLeft
                        
                        val results = FloatArray(1)
                        android.location.Location.distanceBetween(
                            center.latitude, center.longitude,
                            corner.latitude, corner.longitude,
                            results
                        )
                        results[0].toInt().coerceIn(1, 5000)
                    } else 1000
                    onDriftClick(radius)
                },
                modifier = Modifier.testTag("drift_button")
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = true
                ),
                properties = MapProperties(
                    isMyLocationEnabled = true,
                    mapStyleOptions = mapStyleOptions
                )
            ) {
                if (uiState is DriftUiState.PathFound) {
                    val decodedPoints = remember(uiState.polylinePoints) {
                        PolylineDecoder.decode(uiState.polylinePoints).map { 
                            LatLng(it.latitude, it.longitude) 
                        }
                    }
                    
                    // Main Path Polyline
                    Polyline(
                        points = decodedPoints,
                        color = Affectus.colors.primary,
                        width = 15f,
                        geodesic = true
                    )

                    // Path Glow/Border
                    Polyline(
                        points = decodedPoints,
                        color = Affectus.colors.primary.copy(alpha = 0.3f),
                        width = 25f
                    )
                    
                    val startMarker = MarkerUtils.createMarker(Affectus.colors.tertiary, isKeyPoint = true)
                    val endMarker = MarkerUtils.createMarker(Affectus.colors.primary, isKeyPoint = true)
                    val midMarker = MarkerUtils.createMarker(Affectus.colors.secondary, isKeyPoint = false)

                    uiState.stops.forEachIndexed { index, place ->
                        val markerIcon = when (index) {
                            0 -> startMarker
                            uiState.stops.size - 1 -> endMarker
                            else -> midMarker
                        }
                        
                        MarkerInfoWindowContent(
                            state = MarkerState(position = LatLng(place.location.latitude, place.location.longitude)),
                            title = "${index + 1}. ${place.name}",
                            icon = markerIcon
                        ) {
                            PlaceInfoWindow(place = place)
                        }
                    }
                }
            }

            if (uiState is DriftUiState.Error) {
                Snackbar(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Text(text = uiState.message)
                }
            }
        }
    }
}
