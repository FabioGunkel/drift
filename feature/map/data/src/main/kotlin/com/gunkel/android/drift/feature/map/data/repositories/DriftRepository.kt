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
import com.gunkel.android.drift.core.network.api.PlacesV1Api
import com.gunkel.android.drift.feature.map.data.local.dao.CategorySettingDao
import com.gunkel.android.drift.feature.map.data.local.dao.IgnoredPlaceDao
import com.gunkel.android.drift.feature.map.data.local.entities.CategorySettingEntity
import com.gunkel.android.drift.feature.map.data.local.entities.IgnoredPlaceEntity
import com.gunkel.android.drift.feature.map.data.models.Place
import com.gunkel.android.drift.feature.map.data.models.PlaceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DriftRepository(
    private val directionsApi: DirectionsApi,
    private val placesV1Api: PlacesV1Api,
    private val placesClient: PlacesClient,
    private val apiKey: String,
    private val ignoredPlaceDao: IgnoredPlaceDao,
    private val categorySettingDao: CategorySettingDao
) {
    suspend fun getNearbyPlaces(
        lat: Double,
        lng: Double,
        radius: Int,
        includedTypes: List<String>? = null
    ): DataState<List<Place>> = withContext(Dispatchers.IO) {
        try {
            val center = LatLng(lat, lng)
            val circle = CircularBounds.newInstance(center, radius.toDouble())
            
            val placeFields = listOf(
                Field.ID,
                Field.DISPLAY_NAME,
                Field.LOCATION,
                Field.TYPES,
                Field.USER_RATING_COUNT,
                Field.RATING,
                Field.EDITORIAL_SUMMARY,
                Field.FORMATTED_ADDRESS
            )
            
            val typeFilters = includedTypes ?: listOf(
                "historical_place", "tourist_attraction", "museum", "art_gallery",
                "park", "cultural_center", "sculpture", "library"
            )
            
            val request = SearchNearbyRequest.builder(circle, placeFields)
                .setIncludedTypes(typeFilters)
                .setMaxResultCount(20)
                .build()

            val response = placesClient.searchNearby(request).await()
            
            val places = response.places.map { googlePlace ->
                val placeName = googlePlace.displayName ?: "Unknown"
                
                Place(
                    id = googlePlace.id ?: "",
                    name = placeName,
                    location = Location(
                        googlePlace.location?.latitude ?: 0.0,
                        googlePlace.location?.longitude ?: 0.0
                    ),
                    description = googlePlace.editorialSummary ?: googlePlace.formattedAddress,
                    aiSummary = null,
                    type = mapGoogleTypeToDrift(googlePlace.placeTypes),
                    photoMetadata = null, // Photo API remains disabled to avoid costs
                    userRatingsTotal = googlePlace.userRatingCount ?: 0,
                    rating = googlePlace.rating ?: 0.0
                )
            }
            DataState.Success(places)
        } catch (e: Exception) {
            Log.e("DriftRepository", "getNearbyPlaces Error: ${e.message}", e)
            DataState.Error("Failed to fetch nearby places: ${e.message}", e)
        }
    }

    private fun mapGoogleTypeToDrift(types: List<String>?): PlaceType {
        if (types == null) return PlaceType.OTHER
        return when {
            types.contains("museum") || types.contains("art_gallery") -> PlaceType.MUSEUM
            types.contains("park") || types.contains("natural_feature") -> PlaceType.PARK
            types.contains("tourist_attraction") || types.contains("historical_landmark") || types.contains("landmark") -> PlaceType.TOURIST_ATTRACTION
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

    fun getIgnoredPlaces(): Flow<List<IgnoredPlaceEntity>> = ignoredPlaceDao.getAllIgnoredPlaces()

    suspend fun ignorePlace(place: Place) {
        ignoredPlaceDao.insertIgnoredPlace(
            IgnoredPlaceEntity(
                id = place.id,
                title = place.name,
                photoReference = null
            )
        )
    }

    suspend fun removeIgnoredPlace(id: String) = ignoredPlaceDao.deleteById(id)

    suspend fun getIgnoredIds(): List<String> = ignoredPlaceDao.getIgnoredIds()

    fun getCategorySettings(): Flow<List<CategorySettingEntity>> = categorySettingDao.getAllSettings()

    suspend fun updateCategorySetting(categoryName: String, isEnabled: Boolean) {
        categorySettingDao.insertSetting(CategorySettingEntity(categoryName, isEnabled))
    }

    suspend fun isCategoryEnabled(categoryName: String): Boolean {
        return categorySettingDao.isCategoryEnabled(categoryName) ?: true
    }

    suspend fun getPlaceReviewSummary(placeId: String): String? {
        return try {
            val response = placesV1Api.getPlaceDetails(placeId, apiKey)
            response.reviewSummary?.text?.text
        } catch (e: Exception) {
            Log.e("DriftRepository", "Error fetching AI summary: ${e.message}")
            null
        }
    }
}
