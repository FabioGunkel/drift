package com.gunkel.android.drift.feature.map.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.gunkel.android.affectus.components.DriftButton
import com.gunkel.android.affectus.components.DriftInfoWindow
import com.gunkel.android.affectus.theme.Affectus
import com.gunkel.android.affectus.theme.MarkerUtils
import com.gunkel.android.drift.core.common.Location
import com.gunkel.android.drift.core.common.PolylineDecoder
import com.gunkel.android.drift.feature.map.data.models.Place
import com.gunkel.android.drift.feature.map.ui.viewmodels.DriftUiState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun MapContent(
    uiState: DriftUiState,
    onDriftClick: (Int, Location) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-23.5616, -46.6866), 15f)
    }
    
    var selectedPlace by remember { mutableStateOf<Place?>(null) }
    var showDetailsSheet by remember { mutableStateOf(false) }

    val mapStyleOptions = remember(context) {
        MapStyleOptions.loadRawResourceStyle(context, com.gunkel.android.drift.core.ui.R.raw.map_style)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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

    val onPlaceIgnored: (String) -> Unit = { placeName ->
        scope.launch {
            snackbarHostState.showSnackbar(
                message = "Local \"$placeName\" adicionado aos ignorados",
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .semantics { testTagsAsResourceId = true },
        topBar = {
            TopAppBar(
                title = { Text("Drift", style = Affectus.typography.titleLarge) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Configurações")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Affectus.colors.onBackground
                )
            )
        },
        snackbarHost = { 
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 80.dp) // Above DriftButton
            ) 
        },
        floatingActionButton = {
            DriftButton(
                isLoading = uiState is DriftUiState.Loading,
                onClick = {
                    val projection = cameraPositionState.projection
                    val center = cameraPositionState.position.target
                    val searchCenter = Location(center.latitude, center.longitude)
                    
                    val radius = if (projection != null) {
                        val visibleRegion = projection.visibleRegion
                        val center = cameraPositionState.position.target
                        
                        // Calculate average of half-width and half-height as radius
                        val resultsWidth = FloatArray(1)
                        val resultsHeight = FloatArray(1)
                        
                        // Width distance (Center to FarLeft longitude, same latitude)
                        android.location.Location.distanceBetween(
                            center.latitude, center.longitude,
                            center.latitude, visibleRegion.farLeft.longitude,
                            resultsWidth
                        )
                        
                        // Height distance (Center to FarLeft latitude, same longitude)
                        android.location.Location.distanceBetween(
                            center.latitude, center.longitude,
                            visibleRegion.farLeft.latitude, center.longitude,
                            resultsHeight
                        )
                        
                        val radiusAvg = (resultsWidth[0] + resultsHeight[0]) / 2f
                        radiusAvg.toInt()
                    } else 1000
                    onDriftClick(radius, searchCenter)
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
                        
                        MarkerInfoWindow(
                            state = MarkerState(position = LatLng(place.location.latitude, place.location.longitude)),
                            title = "${index + 1}. ${place.name}",
                            icon = markerIcon,
                            onInfoWindowClick = {
                                selectedPlace = place
                                showDetailsSheet = true
                            }
                        ) {
                            DriftInfoWindow {
                                PlaceInfoWindowContent(
                                    place = place,
                                    isClickable = false, // Tooltip content isn't truly interactive
                                    showAddIcon = true,
                                    showAiSummary = false // Only for bottom sheet
                                )
                            }
                        }
                    }
                }
            }

            if (showDetailsSheet && selectedPlace != null) {
                PlaceDetailsBottomSheet(
                    place = selectedPlace!!,
                    onDismiss = { showDetailsSheet = false },
                    onPlaceIgnored = onPlaceIgnored
                )
            }

            if (uiState is DriftUiState.Error) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp), // Match SnackbarHost padding
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Snackbar(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = uiState.message)
                    }
                }
            }
        }
    }
}
