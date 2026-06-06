package com.gunkel.android.drift.feature.map.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.MapStyleOptions
import com.gunkel.android.drift.core.common.PolylineDecoder
import com.gunkel.android.drift.core.ui.R as CoreR
import com.gunkel.android.drift.feature.map.data.models.Place
import com.gunkel.android.drift.feature.map.ui.viewmodels.DriftUiState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.gunkel.android.affectus.theme.AffectusTheme

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
            ExtendedFloatingActionButton(
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
                        results[0].toInt().coerceIn(500, 5000)
                    } else 1000
                    onDriftClick(radius)
                },
                modifier = Modifier.testTag("drift_button"),
                containerColor = AffectusTheme.colors.primary,
                contentColor = AffectusTheme.colors.onPrimary
            ) {
                if (uiState is DriftUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                }
                Text(text = stringResource(id = CoreR.string.drift_button_label))
            }
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
                        color = AffectusTheme.colors.primary,
                        width = 15f,
                        geodesic = true
                    )

                    // Path Glow/Border
                    Polyline(
                        points = decodedPoints,
                        color = AffectusTheme.colors.primary.copy(alpha = 0.3f),
                        width = 25f
                    )
                    
                    uiState.stops.forEachIndexed { index, place ->
                        val markerColor = when (index) {
                            0 -> BitmapDescriptorFactory.HUE_GREEN // Start
                            uiState.stops.size - 1 -> BitmapDescriptorFactory.HUE_RED // End
                            else -> BitmapDescriptorFactory.HUE_AZURE
                        }
                        
                        MarkerInfoWindowContent(
                            state = MarkerState(position = LatLng(place.location.latitude, place.location.longitude)),
                            title = "${index + 1}. ${place.name}",
                            icon = BitmapDescriptorFactory.defaultMarker(markerColor)
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

@Preview(showBackground = true)
@Composable
fun MapContentPreview() {
    MapContent(
        uiState = DriftUiState.Idle,
        onDriftClick = {}
    )
}
