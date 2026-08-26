package com.gunkel.android.drift.feature.map.data.repositories

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place.Field
import com.google.android.libraries.places.api.model.Review
import com.google.android.libraries.places.api.net.FetchResolvedPhotoUriRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.bitmapConfig
import coil3.toBitmap
import com.gunkel.android.drift.core.common.DataState
import com.gunkel.android.drift.core.common.Location
import com.gunkel.android.drift.core.network.api.DirectionsApi
import com.gunkel.android.drift.core.network.api.PlacesV1Api
import com.gunkel.android.drift.feature.map.data.local.dao.IgnoredPlaceDao
import com.gunkel.android.drift.feature.map.data.local.entities.IgnoredPlaceEntity
import com.gunkel.android.drift.feature.map.data.models.Place
import com.gunkel.android.drift.feature.map.data.models.PlaceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DriftRepository(
    private val directionsApi: DirectionsApi,
    private val placesV1Api: PlacesV1Api,
    private val placesClient: PlacesClient,
    private val imageLoader: ImageLoader,
    private val context: Context,
    private val apiKey: String,
    private val ignoredPlaceDao: IgnoredPlaceDao
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
                Field.PHOTO_METADATAS,
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
            
            val places = coroutineScope {
                response.places.map { googlePlace ->
                    async {
                        val placeName = googlePlace.displayName ?: "Unknown"
                        val photoMetadata = googlePlace.photoMetadatas?.firstOrNull()
                        var finalPhoto: Bitmap? = null
                        
                        if (photoMetadata != null) {
                            try {
                                val uriRequest = FetchResolvedPhotoUriRequest.builder(photoMetadata)
                                    .setMaxWidth(800)
                                    .setMaxHeight(800)
                                    .build()
                                val uriResponse = placesClient.fetchResolvedPhotoUri(uriRequest).await()
                                val uri = uriResponse.uri
                                
                                if (uri != null) {
                                    val coilRequest = ImageRequest.Builder(context)
                                        .data(uri.toString())
                                        .allowHardware(false)
                                        .bitmapConfig(Bitmap.Config.ARGB_8888)
                                        .size(400, 400)
                                        .build()
                                    
                                    val result = imageLoader.execute(coilRequest)
                                    val bitmap = result.image?.toBitmap()
                                    
                                    if (bitmap != null) {
                                        // Google Maps InfoWindows use Software Rendering and don't support Hardware Bitmaps.
                                        finalPhoto = if (bitmap.config == Bitmap.Config.HARDWARE) {
                                            bitmap.copy(Bitmap.Config.ARGB_8888, false)
                                        } else {
                                            bitmap
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.w("DriftRepository", "[$placeName] Photo failed: ${e.message}")
                            }
                        }

                        Place(
                            id = googlePlace.id ?: "",
                            name = placeName,
                            location = Location(
                                googlePlace.location?.latitude ?: 0.0,
                                googlePlace.location?.longitude ?: 0.0
                            ),
                            description = googlePlace.editorialSummary,
                            aiSummary = null, // Generative summary not available in current SDK version
                            type = mapGoogleTypeToDrift(googlePlace.placeTypes),
                            photo = finalPhoto,
                            userRatingsTotal = googlePlace.userRatingCount ?: 0,
                            rating = googlePlace.rating ?: 0.0
                        ).let { place ->
                            // Backup: If editorial summary is missing, use formatted address as description
                            if (place.description.isNullOrBlank()) {
                                place.copy(description = googlePlace.formattedAddress)
                            } else {
                                place
                            }
                        }
                    }
                }.awaitAll()
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
                photoReference = null // In a real scenario, we might want to store a photo reference or URL
            )
        )
    }

    suspend fun removeIgnoredPlace(id: String) = ignoredPlaceDao.deleteById(id)

    suspend fun getIgnoredIds(): List<String> = ignoredPlaceDao.getIgnoredIds()

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
