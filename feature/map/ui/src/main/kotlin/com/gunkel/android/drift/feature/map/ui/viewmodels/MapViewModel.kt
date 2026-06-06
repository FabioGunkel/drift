package com.gunkel.android.drift.feature.map.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gunkel.android.drift.core.common.DataState
import com.gunkel.android.drift.core.common.Location
import com.gunkel.android.drift.feature.map.data.models.Place
import com.gunkel.android.drift.feature.map.domain.usecases.GetDriftWalkingPathUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val getDriftWalkingPathUseCase: GetDriftWalkingPathUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DriftUiState>(DriftUiState.Idle)
    val uiState: StateFlow<DriftUiState> = _uiState.asStateFlow()

    fun onDriftClicked(currentLocation: Location, radius: Int = 0) {
        viewModelScope.launch {
            _uiState.value = DriftUiState.Loading
            val result = getDriftWalkingPathUseCase(currentLocation, radius)
            
            _uiState.value = when (result) {
                is DataState.Success -> DriftUiState.PathFound(
                    stops = result.data.stops,
                    polylinePoints = result.data.polylinePoints
                )
                is DataState.Error -> DriftUiState.Error(result.message)
                DataState.Loading -> DriftUiState.Loading
            }
        }
    }
}

sealed class DriftUiState {
    data object Idle : DriftUiState()
    data object Loading : DriftUiState()
    data class PathFound(val stops: List<Place>, val polylinePoints: String) : DriftUiState()
    data class Error(val message: String) : DriftUiState()
}
