package com.gunkel.android.drift.feature.map.domain.usecases

import com.gunkel.android.drift.core.common.DataState
import com.gunkel.android.drift.core.common.Location
import com.gunkel.android.drift.feature.map.data.models.DriftPath
import com.gunkel.android.drift.feature.map.data.repositories.DriftRepository

class GetDriftWalkingPathUseCase(
    private val calculateStopsUseCase: CalculateDriftPathUseCase,
    private val repository: DriftRepository
) {
    suspend operator fun invoke(userLocation: Location, radius: Int = 1000): DataState<DriftPath> {
        val stopsResult = calculateStopsUseCase(userLocation, radius)
        if (stopsResult !is DataState.Success) return stopsResult as DataState<DriftPath>
        
        val stops = stopsResult.data
        if (stops.size < 2) return DataState.Error("Not enough places to drift")

        val origin = "${userLocation.latitude},${userLocation.longitude}"
        val destination = "${stops.last().location.latitude},${stops.last().location.longitude}"
        val waypoints = stops.dropLast(1).map { "${it.location.latitude},${it.location.longitude}" }

        val polylineResult = repository.getPathDirections(origin, destination, waypoints)
        
        return when (polylineResult) {
            is DataState.Success -> DataState.Success(DriftPath(stops, polylineResult.data))
            is DataState.Error -> DataState.Error("Path calculated, but directions failed: ${polylineResult.message}")
            DataState.Loading -> DataState.Loading
        }
    }
}
