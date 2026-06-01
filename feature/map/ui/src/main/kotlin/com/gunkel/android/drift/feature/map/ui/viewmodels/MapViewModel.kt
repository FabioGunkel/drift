package com.gunkel.android.drift.feature.map.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gunkel.android.drift.core.common.DataState
import com.gunkel.android.drift.feature.map.domain.models.Place
import com.gunkel.android.drift.feature.map.domain.usecases.GetNearbyPlacesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getNearbyPlacesUseCase: GetNearbyPlacesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DataState<List<Place>>>(DataState.Loading)
    val uiState: StateFlow<DataState<List<Place>>> = _uiState.asStateFlow()

    fun fetchPlaces(lat: Double, lng: Double) {
        viewModelScope.launch {
            _uiState.value = DataState.Loading
            val result = getNearbyPlacesUseCase(lat, lng, 5000)
            _uiState.value = result
        }
    }
}
