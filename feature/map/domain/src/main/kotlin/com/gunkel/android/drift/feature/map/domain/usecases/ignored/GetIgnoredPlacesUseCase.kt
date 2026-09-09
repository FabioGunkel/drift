package com.gunkel.android.drift.feature.map.domain.usecases.ignored

import com.gunkel.android.drift.feature.map.data.local.entities.IgnoredPlaceEntity
import com.gunkel.android.drift.feature.map.data.repositories.DriftRepository
import kotlinx.coroutines.flow.Flow

class GetIgnoredPlacesUseCase(
    private val repository: DriftRepository
) {
    operator fun invoke(): Flow<List<IgnoredPlaceEntity>> {
        return repository.getIgnoredPlaces()
    }
}
