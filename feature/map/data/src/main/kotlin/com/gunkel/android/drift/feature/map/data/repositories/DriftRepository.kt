package com.gunkel.android.drift.feature.map.data.repositories

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place.Field
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.gunkel.android.drift.core.common.DataState
import com.gunkel.android.drift.core.common.Location
import com.gunkel.android.drift.core.network.api.DirectionsApi
import com.gunkel.android.drift.feature.map.data.models.Place
import com.gunkel.android.drift.feature.map.data.models.PlaceType
import kotlinx.coroutines.tasks.await

class DriftRepository(
    private val directionsApi: DirectionsApi,
    private val placesClient: PlacesClient,
    private val apiKey: String
) {
    suspend fun getNearbyPlaces(
        lat: Double,
        lng: Double,
        radius: Int,
        includedTypes: List<String>? = null
    ): DataState<List<Place>> {
        return try {
            val center = LatLng(lat, lng)
            val circle = CircularBounds.newInstance(center, radius.toDouble())
            
            val placeFields = listOf(
                Field.ID,
                Field.DISPLAY_NAME,
                Field.LOCATION,
                Field.TYPES,
                Field.USER_RATING_COUNT,
                Field.RATING
            )
            
            // If types are provided, use them; otherwise use a default broad set
            val typeFilters = includedTypes ?: listOf(
                "historical_place", "tourist_attraction", "museum", "art_gallery",
                "park", "cultural_center", "sculpture", "library"
            )
            
            val request = SearchNearbyRequest.builder(circle, placeFields)
                .setIncludedTypes(typeFilters)
                .setMaxResultCount(20) // API limit is 20 for searchNearby
                .build()

            val response = placesClient.searchNearby(request).await()
            Log.d("DriftRepository", "Radius $radius m: Found ${response.places.size} raw places for types $typeFilters")
            
            val places = response.places.map { googlePlace ->
                Log.d("DriftRepository", "Place: ${googlePlace.displayName}, Types: ${googlePlace.placeTypes}, Ratings: ${googlePlace.userRatingCount}")
                Place(
                    id = googlePlace.id ?: "",
                    name = googlePlace.displayName ?: "Unknown",
                    location = Location(
                        googlePlace.location?.latitude ?: 0.0,
                        googlePlace.location?.longitude ?: 0.0
                    ),
                    type = mapGoogleTypeToDrift(googlePlace.placeTypes),
                    userRatingsTotal = googlePlace.userRatingCount ?: 0,
                    rating = googlePlace.rating ?: 0.0
                )
            }
            DataState.Success(places)
        } catch (e: Exception) {
            DataState.Error("Failed to fetch nearby places: ${e.message}", e)
        }
    }

    private fun mapGoogleTypeToDrift(types: List<String>?): PlaceType {
        if (types == null) return PlaceType.OTHER
        return when {
            types.contains("museum") || types.contains("art_gallery") -> PlaceType.MUSEUM
            types.contains("park") || types.contains("natural_feature") || types.contains("town_square") -> PlaceType.PARK
            types.contains("tourist_attraction") || types.contains("historical_landmark") || types.contains("landmark") || types.contains("historical_place") -> PlaceType.TOURIST_ATTRACTION
            types.contains("restaurant") || types.contains("cafe") || types.contains("food") || types.contains("bar") -> PlaceType.RESTAURANT
            types.contains("store") || types.contains("shopping_mall") || types.contains("clothing_store") || types.contains("supermarket") -> PlaceType.STORE
            else -> PlaceType.OTHER
        }
    }

    suspend fun getPathDirections(origin: String, destination: String, waypoints: List<String>): DataState<String> {
        return try {
            val waypointsString = waypoints.joinToString("|")
            val response = directionsApi.getDirections(origin, destination, waypointsString, apiKey = apiKey)
            if (response.status == "OK" && response.routes.isNotEmpty()) {
                DataState.Success(response.routes[0].overviewPolyline.points)
            } else {
                val msg = response.errorMessage ?: response.status
                DataState.Error("Directions API error: $msg")
            }
        } catch (e: Exception) {
            DataState.Error("Failed to fetch directions", e)
        }
    }
}
