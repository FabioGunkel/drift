package com.gunkel.android.drift.feature.map.domain.usecases

import com.gunkel.android.drift.core.common.DataState
import com.gunkel.android.drift.core.common.Location
import com.gunkel.android.drift.core.common.distanceTo
import com.gunkel.android.drift.feature.map.data.models.DriftPath
import com.gunkel.android.drift.feature.map.data.repositories.DriftRepository

class GetDriftWalkingPathUseCase(
    private val calculateStopsUseCase: CalculateDriftPathUseCase,
    private val repository: DriftRepository
) {
    suspend operator fun invoke(searchCenter: Location, userLocation: Location?, radius: Int = 0): DataState<DriftPath> {
        // Logic: determine if user is "near" enough to be the start point
        // Threshold is the search radius. If user is inside, they are the start.
        val startLocation = if (userLocation != null && userLocation.distanceTo(searchCenter) <= radius) {
            userLocation
        } else {
            null
        }

        val stopsResult = calculateStopsUseCase(searchCenter, startLocation, radius)
        if (stopsResult !is DataState.Success) return stopsResult as DataState<DriftPath>
        
        val stops = stopsResult.data
        if (stops.isEmpty()) return DataState.Error("No relevant places found")
        if (stops.size < 2 && startLocation == null) return DataState.Error("Not enough places to drift")

        // Origin for directions:
        // If startLocation is non-null, use it. Otherwise, use the first stop.
        val origin = if (startLocation != null) {
            "${startLocation.latitude},${startLocation.longitude}"
        } else {
            "${stops.first().location.latitude},${stops.first().location.longitude}"
        }

        val destination = "${stops.last().location.latitude},${stops.last().location.longitude}"
        
        // Waypoints: all stops except the one used as origin or destination
        val waypointList = if (startLocation != null) {
            stops.dropLast(1)
        } else {
            stops.drop(1).dropLast(1)
        }
        val waypoints = waypointList.map { "${it.location.latitude},${it.location.longitude}" }

        val polylineResult = repository.getPathDirections(origin, destination, waypoints)
        
        return when (polylineResult) {
            is DataState.Success -> DataState.Success(DriftPath(stops, polylineResult.data))
            is DataState.Error -> DataState.Error("Path calculated, but directions failed: ${polylineResult.message}")
            DataState.Loading -> DataState.Loading
        }
    }
}
