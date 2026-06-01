package com.gunkel.android.drift.feature.map.data.models

data class DriftPath(
    val stops: List<Place>,
    val polylinePoints: String
)
