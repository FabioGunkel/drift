package com.gunkel.android.drift.feature.map.domain.usecases.ignored

import com.gunkel.android.drift.feature.map.data.models.Place
import com.gunkel.android.drift.feature.map.data.repositories.DriftRepository

class AddIgnoredPlaceUseCase(
    private val repository: DriftRepository
) {
    suspend operator fun invoke(place: Place) {
        repository.ignorePlace(place)
    }
}
