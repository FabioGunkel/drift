package com.gunkel.android.drift.feature.map.domain.usecases.ignored

import com.gunkel.android.drift.feature.map.data.repositories.DriftRepository

class RemoveIgnoredPlaceUseCase(
    private val repository: DriftRepository
) {
    suspend operator fun invoke(id: String) {
        repository.removeIgnoredPlace(id)
    }
}
