package com.gunkel.android.drift.feature.map.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gunkel.android.drift.feature.map.data.local.entities.CategorySettingEntity
import com.gunkel.android.drift.feature.map.data.models.PlaceType
import com.gunkel.android.drift.feature.map.data.repositories.DriftRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: DriftRepository
) : ViewModel() {

    val categorySettings: StateFlow<List<CategorySettingEntity>> = repository.getCategorySettings()
        .map { settings ->
            val existing = settings.associateBy { it.categoryName }
            listOf(
                PlaceType.MUSEUM,
                PlaceType.PARK,
                PlaceType.TOURIST_ATTRACTION,
                PlaceType.HISTORIC_SITE,
                PlaceType.RESTAURANT
            ).map { type ->
                existing[type.name] ?: CategorySettingEntity(type.name, true)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onCategoryToggle(categoryName: String, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.updateCategorySetting(categoryName, isEnabled)
        }
    }
}
