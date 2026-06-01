package com.gunkel.android.drift.feature.map.domain.usecases

import com.gunkel.android.drift.core.common.DataState
import com.gunkel.android.drift.core.domain.models.Location
import com.gunkel.android.drift.feature.map.data.models.Place
import com.gunkel.android.drift.feature.map.data.models.PlaceType
import com.gunkel.android.drift.feature.map.data.repositories.DriftRepository
import kotlin.math.sqrt

class CalculateDriftPathUseCase(
    private val repository: DriftRepository
) {
    suspend operator fun invoke(userLocation: Location, radius: Int): DataState<List<Place>> {
        val placesResult = repository.getNearbyPlaces(userLocation.latitude, userLocation.longitude, radius)
        
        if (placesResult !is DataState.Success) return placesResult as DataState<List<Place>>
        
        val allPlaces = placesResult.data
        if (allPlaces.isEmpty()) return DataState.Error("No places found nearby")
        
        // 1. Weighted Greedy Selection
        val path = mutableListOf<Place>()
        var currentLoc = userLocation
        val remainingPlaces = allPlaces.toMutableList()
        
        // We want a path of 5-7 stops
        val pathSize = minOf(7, allPlaces.size)
        
        repeat(pathSize) {
            val nextPlace = selectNextBestPlace(currentLoc, remainingPlaces, path)
            if (nextPlace != null) {
                path.add(nextPlace)
                remainingPlaces.remove(nextPlace)
                currentLoc = Location(nextPlace.location.latitude, nextPlace.location.longitude)
            }
        }
        
        // 2. 2-Opt Refinement (Uncrossing)
        val optimizedPath = optimizePath2Opt(path)
        
        return DataState.Success(optimizedPath)
    }

    private fun selectNextBestPlace(currentLoc: Location, remaining: List<Place>, currentPath: List<Place>): Place? {
        return remaining.maxByOrNull { place ->
            calculateUtility(currentLoc, place, currentPath)
        }
    }

    private fun calculateUtility(currentLoc: Location, place: Place, currentPath: List<Place>): Double {
        val distance = calculateEuclideanDistance(currentLoc, Location(place.location.latitude, place.location.longitude))
        val baseScore = when (place.type) {
            PlaceType.HISTORIC_SITE -> 100.0
            PlaceType.PARK -> 80.0
            PlaceType.MUSEUM -> 60.0
            PlaceType.OTHER -> 40.0 // Assuming food/restaurants are mapped to OTHER for now
            else -> 20.0
        }
        
        // Variety/Safety Bonus: If the last 2 stops were NOT parks/utility, boost parks/food
        val lastTwoTypes = currentPath.takeLast(2).map { it.type }
        val needsVariety = lastTwoTypes.isNotEmpty() && lastTwoTypes.all { it == PlaceType.HISTORIC_SITE || it == PlaceType.MUSEUM }
        
        val varietyBonus = if (needsVariety && (place.type == PlaceType.PARK || place.type == PlaceType.OTHER)) 50.0 else 0.0
        
        // Utility = (Score + Bonus) / (1 + Distance)
        return (baseScore + varietyBonus) / (1.0 + distance * 1000) // distance in KM, scale penalty
    }

    private fun calculateEuclideanDistance(loc1: Location, loc2: Location): Double {
        val dLat = loc1.latitude - loc2.latitude
        val dLng = loc1.longitude - loc2.longitude
        return sqrt(dLat * dLat + dLng * dLng)
    }

    private fun optimizePath2Opt(path: List<Place>): List<Place> {
        if (path.size < 4) return path
        
        val mutablePath = path.toMutableList()
        var improved = true
        
        while (improved) {
            improved = false
            for (i in 0 until mutablePath.size - 1) {
                for (j in i + 2 until mutablePath.size) {
                    if (j == mutablePath.size - 1) continue // Simplified for non-loop path
                    
                    val dist1 = calculateEuclideanDistance(Location(mutablePath[i].location.latitude, mutablePath[i].location.longitude), Location(mutablePath[i+1].location.latitude, mutablePath[i+1].location.longitude)) +
                                calculateEuclideanDistance(Location(mutablePath[j].location.latitude, mutablePath[j].location.longitude), Location(mutablePath[j+1].location.latitude, mutablePath[j+1].location.longitude))
                    
                    val dist2 = calculateEuclideanDistance(Location(mutablePath[i].location.latitude, mutablePath[i].location.longitude), Location(mutablePath[j].location.latitude, mutablePath[j].location.longitude)) +
                                calculateEuclideanDistance(Location(mutablePath[i+1].location.latitude, mutablePath[i+1].location.longitude), Location(mutablePath[j+1].location.latitude, mutablePath[j+1].location.longitude))
                    
                    if (dist2 < dist1) {
                        // Swap i+1 to j
                        mutablePath.subList(i + 1, j + 1).reverse()
                        improved = true
                    }
                }
            }
        }
        return mutablePath
    }
}
