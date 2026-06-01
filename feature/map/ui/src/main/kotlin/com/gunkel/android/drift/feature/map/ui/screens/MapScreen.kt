package com.gunkel.android.drift.feature.map.ui.screens

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val fusedLocationClient = remember(context) { 
        LocationServices.getFusedLocationProviderClient(context) 
    }

    MapContent(
        uiState = uiState,
        onDriftClick = {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.onDriftClicked(Location(location.latitude, location.longitude))
                }
            }
        },
        modifier = modifier
    )
}
