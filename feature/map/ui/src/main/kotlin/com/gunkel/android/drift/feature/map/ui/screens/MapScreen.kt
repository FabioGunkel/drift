package com.gunkel.android.drift.feature.map.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.gunkel.android.drift.feature.map.ui.components.MapContent
import com.gunkel.android.drift.feature.map.ui.viewmodels.MapViewModel

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchPlaces(0.0, 0.0) // Dummy coordinates
    }

    MapContent(uiState = uiState)
}
