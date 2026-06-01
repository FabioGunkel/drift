package com.gunkel.android.drift.core.network.api

import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApi {
    @GET("maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("waypoints") waypoints: String,
        @Query("mode") mode: String = "walking",
        @Query("key") apiKey: String
    ): DirectionsResponse
}

data class DirectionsResponse(
    val routes: List<Route>,
    val status: String
)

data class Route(
    val overview_polyline: Polyline
)

data class Polyline(
    val points: String
)
