package com.gunkel.android.drift.feature.map.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gunkel.android.drift.feature.map.data.models.Place
import com.gunkel.android.drift.feature.map.domain.usecases.GetPlaceAiSummaryUseCase
import com.gunkel.android.drift.feature.map.domain.usecases.ignored.AddIgnoredPlaceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaceDetailsViewModel(
    private val getPlaceAiSummaryUseCase: GetPlaceAiSummaryUseCase,
    private val addIgnoredPlaceUseCase: AddIgnoredPlaceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaceDetailsUiState>(PlaceDetailsUiState.Idle)
    val uiState: StateFlow<PlaceDetailsUiState> = _uiState.asStateFlow()

    fun loadPlaceDetails(place: Place) {
        _uiState.value = PlaceDetailsUiState.Loading(place)
        viewModelScope.launch {
            val aiSummary = getPlaceAiSummaryUseCase(place.id)
            _uiState.value = PlaceDetailsUiState.Success(
                place = place.copy(aiSummary = aiSummary)
            )
        }
    }

    fun ignorePlace(place: Place) {
        viewModelScope.launch {
            addIgnoredPlaceUseCase(place)
            _uiState.value = PlaceDetailsUiState.PlaceIgnored(place.name)
        }
    }
}

sealed class PlaceDetailsUiState {
    data object Idle : PlaceDetailsUiState()
    data class Loading(val place: Place) : PlaceDetailsUiState()
    data class Success(val place: Place) : PlaceDetailsUiState()
    data class PlaceIgnored(val placeName: String) : PlaceDetailsUiState()
}
