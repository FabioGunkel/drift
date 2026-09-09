package com.gunkel.android.drift.feature.map.ui.screens

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import com.gunkel.android.drift.core.common.Location
import com.gunkel.android.drift.feature.map.ui.components.MapContent
import com.gunkel.android.drift.feature.map.ui.viewmodels.MapViewModel
import org.koin.androidx.compose.koinViewModel

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val fusedLocationClient = remember(context) { 
        LocationServices.getFusedLocationProviderClient(context) 
    }

    var userLocation by remember { mutableStateOf<Location?>(null) }

    LaunchedEffect(Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                userLocation = Location(location.latitude, location.longitude)
            }
        }
    }

    MapContent(
        uiState = uiState,
        onDriftClick = { radius, mapCenter ->
            viewModel.onDriftClicked(mapCenter, userLocation, radius)
        },
        onNavigateToSettings = onNavigateToSettings,
        modifier = modifier
    )
}
