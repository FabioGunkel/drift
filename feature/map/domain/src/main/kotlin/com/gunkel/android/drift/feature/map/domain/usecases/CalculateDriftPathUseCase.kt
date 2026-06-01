package com.gunkel.android.drift.feature.map.domain.usecases

import android.util.Log
import com.gunkel.android.drift.core.common.DataState
import com.gunkel.android.drift.core.common.Location
import com.gunkel.android.drift.feature.map.data.models.Place
import com.gunkel.android.drift.feature.map.data.models.PlaceType
import com.gunkel.android.drift.feature.map.data.repositories.DriftRepository
import kotlin.math.log10
import kotlin.math.sqrt

class CalculateDriftPathUseCase(
    private val repository: DriftRepository
) {
    suspend operator fun invoke(userLocation: Location, radius: Int = 1000): DataState<List<Place>> {
        Log.d("DriftUseCase", "Starting Drift calculation from: $userLocation with radius: $radius")
        
        // 1. Fetch Landmarks (historic, touristic, parks, museums)
        // Note: Using only Table A types supported by Search Nearby (New)
        val landmarkTypes = listOf(
            "tourist_attraction", "museum", "art_gallery", "park", "library", "church", "historical_place", "market"
        )
        val landmarksResult = repository.getNearbyPlaces(userLocation.latitude, userLocation.longitude, radius, landmarkTypes)
        
        // 2. Fetch Restaurants
        val restaurantTypes = listOf("restaurant", "cafe", "bar")
        val restaurantsResult = repository.getNearbyPlaces(userLocation.latitude, userLocation.longitude, radius, restaurantTypes)
        
        if (landmarksResult is DataState.Error) {
            return DataState.Error("Failed to fetch landmarks: ${landmarksResult.message}")
        }
        
        // Filter out any "RESTAURANT" or "OTHER" from landmarks just in case API returns them
        val allLandmarks = (landmarksResult as DataState.Success).data
            .filter { it.type != PlaceType.RESTAURANT && it.type != PlaceType.OTHER && it.type != PlaceType.STORE }
        
        // Ensure restaurantsResult only contains actual restaurants/cafes
        val allRestaurants = if (restaurantsResult is DataState.Success) {
            restaurantsResult.data.filter { it.type == PlaceType.RESTAURANT }
        } else emptyList()
        
        // 3. Select top 10 landmarks using weighted scoring
        val top10Landmarks = allLandmarks
            .sortedByDescending { calculateScore(it, userLocation, radius) }
            .take(10)
            
        // 4. Select top 3 restaurants using weighted scoring
        val top3Restaurants = allRestaurants
            .sortedByDescending { calculateScore(it, userLocation, radius) }
            .take(3)
            
        val selectedStops = (top10Landmarks + top3Restaurants).toMutableList()
        
        if (selectedStops.isEmpty()) {
            return DataState.Error("No relevant places found nearby within 2km")
        }
        
        Log.d("DriftUseCase", "Selected ${top10Landmarks.size} landmarks and ${top3Restaurants.size} restaurants")
        selectedStops.forEach { 
            val score = calculateScore(it, userLocation, radius)
            Log.d("DriftUseCase", "Selected: ${it.name} (${it.type}), Score: %.2f, Ratings: ${it.userRatingsTotal}, Rating: ${it.rating}".format(score))
        }

        // 5. Create an initial path using nearest neighbor to provide a starting point for optimization
        val initialPath = buildNearestNeighborPath(userLocation, selectedStops)
        
        // 6. Optimize the path using 2-Opt
        val optimizedPath = optimizePath2Opt(userLocation, initialPath)
        
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
        // Approximation: 1 degree latitude ~ 111km, 1 degree longitude ~ 111km * cos(lat)
        // For 2km radius, simple Euclidean on lat/lng is okay for relative ranking if we just want comparison.
        // However, to be more precise for "distance / radius" normalization, we should use meters.
        return sqrt(dLat * dLat + dLng * dLng)
    }

    private fun calculateDistanceInMeters(loc1: Location, loc2: Location): Double {
        val earthRadius = 6371000.0 // meters
        val dLat = Math.toRadians(loc2.latitude - loc1.latitude)
        val dLng = Math.toRadians(loc2.longitude - loc1.longitude)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(loc1.latitude)) * Math.cos(Math.toRadians(loc2.latitude)) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2)
        val c = 2 * Math.atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    private fun calculateScore(place: Place, userLocation: Location, maxRadius: Int): Double {
        // Normalization:
        // 1. Popularity: log10(userRatingsTotal + 1) capped at log10(1001) to favor local gems
        val cappedRatings = minOf(place.userRatingsTotal.toDouble(), 1000.0)
        val normPopularity = log10(cappedRatings + 1.0) / log10(1001.0)
        
        // 2. Rating: rating / 5.0
        val normRating = place.rating / 5.0
        
        // 3. Proximity: 1.0 - (distance / maxRadius)
        val distance = calculateDistanceInMeters(userLocation, Location(place.location.latitude, place.location.longitude))
        val normProximity = (1.0 - (distance / maxRadius.toDouble())).coerceIn(0.0, 1.0)
        
        // Weights: 0.3 Pop, 0.3 Rating, 0.4 Proximity
        return (0.3 * normPopularity) + (0.3 * normRating) + (0.4 * normProximity)
    }

    private fun optimizePath2Opt(start: Location, path: List<Place>): List<Place> {
        if (path.size < 2) return path
        
        // We include the start location in distance calculations to ensure the first leg is also optimized
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
                    
                    // Current distance: (p1-p2) + (p3-p4)
                    var currentDist = calculateEuclideanDistance(p1, p2)
                    if (p4 != null) {
                        currentDist += calculateEuclideanDistance(p3, p4)
                    }
                    
                    // New distance if we swap: (p1-p3) + (p2-p4)
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
