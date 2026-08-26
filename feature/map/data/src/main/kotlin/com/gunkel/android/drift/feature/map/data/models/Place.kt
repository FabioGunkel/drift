package com.gunkel.android.drift.feature.map.data.models

import com.gunkel.android.drift.core.common.Location

data class Place(
    val id: String,
    val name: String,
    val location: Location,
    val description: String? = null,
    val aiSummary: String? = null,
    val type: PlaceType,
    val photo: Any? = null, // Can be Bitmap, String (URL), or Uri
    val userRatingsTotal: Int = 0,
    val rating: Double = 0.0
)

enum class PlaceType {
    MUSEUM, PARK, TOURIST_ATTRACTION, HISTORIC_SITE, RESTAURANT, STORE, OTHER
}
