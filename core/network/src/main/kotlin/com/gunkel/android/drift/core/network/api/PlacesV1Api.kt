package com.gunkel.android.drift.core.network.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface PlacesV1Api {
    @GET("https://places.googleapis.com/v1/places/{placeId}")
    suspend fun getPlaceDetails(
        @Path("placeId") placeId: String,
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String = "displayName,reviewSummary"
    ): PlaceDetailsResponse
}

data class PlaceDetailsResponse(
    val displayName: DisplayNameResponse?,
    val reviewSummary: ReviewSummaryResponse?
)

data class DisplayNameResponse(
    val text: String?,
    val languageCode: String?
)

data class ReviewSummaryResponse(
    val text: LocalizedTextResponse?,
    val flagContentUri: String?,
    val disclosureText: LocalizedTextResponse?,
    val reviewsUri: String?
)

data class LocalizedTextResponse(
    val text: String?,
    val languageCode: String?
)
