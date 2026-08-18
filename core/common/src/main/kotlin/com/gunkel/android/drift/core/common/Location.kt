package com.gunkel.android.drift.core.common

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Location(
    val latitude: Double,
    val longitude: Double,
    val name: String? = null
)

fun Location.distanceTo(other: Location): Double {
    val earthRadius = 6371000.0 // meters
    val dLat = Math.toRadians(other.latitude - this.latitude)
    val dLng = Math.toRadians(other.longitude - this.longitude)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(this.latitude)) * cos(Math.toRadians(other.latitude)) *
            sin(dLng / 2) * sin(dLng / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}
