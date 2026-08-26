package com.gunkel.android.drift.feature.map.domain.usecases

import android.util.Log
import com.gunkel.android.drift.core.common.DataState
import com.gunkel.android.drift.core.common.Location
import com.gunkel.android.drift.core.common.distanceTo
import com.gunkel.android.drift.feature.map.data.models.Place
import com.gunkel.android.drift.feature.map.data.models.PlaceType
import com.gunkel.android.drift.feature.map.data.repositories.DriftRepository
import kotlin.math.log10
import kotlin.math.sqrt

class CalculateDriftPathUseCase(
    private val repository: DriftRepository
) {
    suspend operator fun invoke(
        searchCenter: Location,
        startLocation: Location?,
        radius: Int = 0
    ): DataState<List<Place>> {
        Log.d("DriftUseCase", "Starting Drift calculation centered at: $searchCenter, starting at: $startLocation, radius: $radius")
        
        val ignoredIds = repository.getIgnoredIds()
        
        val landmarkTypes = listOf(
            "tourist_attraction", "museum", "art_gallery", "park", "library", "church", "historical_landmark", 
            "market"
        )
        val landmarksResult = repository.getNearbyPlaces(searchCenter.latitude, searchCenter.longitude, radius, landmarkTypes)
        
        val restaurantTypes = listOf("restaurant", "cafe", "bar")
        val restaurantsResult = repository.getNearbyPlaces(searchCenter.latitude, searchCenter.longitude, radius, restaurantTypes)
        
        if (landmarksResult is DataState.Error) {
            return DataState.Error("Failed to fetch landmarks: ${landmarksResult.message}")
        }
        
        val allLandmarks = (landmarksResult as DataState.Success).data
            .filter { it.type != PlaceType.RESTAURANT && it.type != PlaceType.OTHER && it.type != PlaceType.STORE }
            .filter { it.id !in ignoredIds }
        
        val top10Landmarks = allLandmarks
            .sortedByDescending { calculateScore(it, searchCenter, radius) }
            .take(10)

        // Check if there are already restaurants/pubs in the landmarks list
        val hasRestaurantsInLandmarks = top10Landmarks.any { it.type == PlaceType.RESTAURANT }
        
        val selectedStops = top10Landmarks.toMutableList()

        if (!hasRestaurantsInLandmarks) {
            val allRestaurants = if (restaurantsResult is DataState.Success) {
                restaurantsResult.data.filter { it.type == PlaceType.RESTAURANT && it.id !in ignoredIds }
            } else emptyList()

            val top3Restaurants = allRestaurants
                .sortedByDescending { calculateScore(it, searchCenter, radius) }
                .take(3)
            
            selectedStops.addAll(top3Restaurants)
            Log.d("DriftUseCase", "Added ${top3Restaurants.size} extra restaurants as none were found in landmarks")
        }
        
        if (selectedStops.isEmpty()) {
            return DataState.Error("No relevant places found nearby")
        }
        
        Log.d("DriftUseCase", "Selected total of ${selectedStops.size} stops")

        val effectiveStart = startLocation ?: searchCenter
        val initialPath = buildNearestNeighborPath(effectiveStart, selectedStops)
        val optimizedPath = optimizePath2Opt(effectiveStart, initialPath)
        
        return DataState.Success(optimizedPath)
    }

    private fun buildNearestNeighborPath(start: Location, stops: List<Place>): List<Place> {
        val path = mutableListOf<Place>()
        var currentLoc = start
        val remaining = stops.toMutableList()
        
        while (remaining.isNotEmpty()) {
            val next = remaining.minByOrNull { calculateEuclideanDistance(currentLoc, Location(it.location.latitude, it.location.longitude)) }
            if (next != null) {
                path.add(next)
                remaining.remove(next)
                currentLoc = Location(next.location.latitude, next.location.longitude)
            } else {
                break
            }
        }
        return path
    }

    private fun calculateEuclideanDistance(loc1: Location, loc2: Location): Double {
        val dLat = loc1.latitude - loc2.latitude
        val dLng = loc1.longitude - loc2.longitude
        return sqrt(dLat * dLat + dLng * dLng)
    }

    private fun calculateScore(place: Place, center: Location, maxRadius: Int): Double {
        val cappedRatings = minOf(place.userRatingsTotal.toDouble(), 1000.0)
        val normPopularity = log10(cappedRatings + 1.0) / log10(1001.0)
        
        val normRating = place.rating / 5.0
        
        val distance = center.distanceTo(Location(place.location.latitude, place.location.longitude))
        val normProximity = (1.0 - (distance / maxRadius.toDouble())).coerceIn(0.0, 1.0)
        
        return (0.3 * normPopularity) + (0.3 * normRating) + (0.4 * normProximity)
    }

    private fun optimizePath2Opt(start: Location, path: List<Place>): List<Place> {
        if (path.size < 2) return path
        
        val mutablePath = path.toMutableList()
        var improved = true
        
        while (improved) {
            improved = false
            for (i in -1 until mutablePath.size - 1) {
                for (j in i + 2 until mutablePath.size) {
                    val p1 = if (i == -1) start else Location(mutablePath[i].location.latitude, mutablePath[i].location.longitude)
                    val p2 = Location(mutablePath[i + 1].location.latitude, mutablePath[i + 1].location.longitude)
                    val p3 = Location(mutablePath[j].location.latitude, mutablePath[j].location.longitude)
                    val p4 = if (j + 1 == mutablePath.size) null else Location(mutablePath[j + 1].location.latitude, mutablePath[j + 1].location.longitude)
                    
                    var currentDist = calculateEuclideanDistance(p1, p2)
                    if (p4 != null) {
                        currentDist += calculateEuclideanDistance(p3, p4)
                    }
                    
                    var newDist = calculateEuclideanDistance(p1, p3)
                    if (p4 != null) {
                        newDist += calculateEuclideanDistance(p2, p4)
                    }
                    
                    if (newDist < currentDist) {
                        mutablePath.subList(i + 1, j + 1).reverse()
                        improved = true
                    }
                }
            }
        }
        return mutablePath
    }
}
