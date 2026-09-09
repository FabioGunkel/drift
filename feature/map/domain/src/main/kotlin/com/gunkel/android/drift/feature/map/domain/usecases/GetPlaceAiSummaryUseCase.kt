package com.gunkel.android.drift.feature.map.domain.usecases

import com.gunkel.android.drift.feature.map.data.repositories.DriftRepository

class GetPlaceAiSummaryUseCase(
    private val repository: DriftRepository
) {
    suspend operator fun invoke(placeId: String): String? {
        return repository.getPlaceReviewSummary(placeId)
    }
}
