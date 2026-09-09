package com.gunkel.android.drift.feature.map.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gunkel.android.drift.feature.map.domain.usecases.ignored.GetIgnoredPlacesUseCase
import com.gunkel.android.drift.feature.map.domain.usecases.ignored.RemoveIgnoredPlaceUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IgnoredPlacesViewModel(
    private val getIgnoredPlacesUseCase: GetIgnoredPlacesUseCase,
    private val removeIgnoredPlaceUseCase: RemoveIgnoredPlaceUseCase
) : ViewModel() {

    val ignoredPlaces = getIgnoredPlacesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onRemoveClicked(id: String) {
        viewModelScope.launch {
            removeIgnoredPlaceUseCase(id)
        }
    }
}
