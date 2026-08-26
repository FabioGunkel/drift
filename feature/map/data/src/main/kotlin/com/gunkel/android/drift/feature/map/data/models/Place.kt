package com.gunkel.android.drift.feature.map.data.models

import com.google.android.libraries.places.api.model.PhotoMetadata
import com.gunkel.android.drift.core.common.Location

data class Place(
    val id: String,
    val name: String,
    val location: Location,
    val description: String? = null,
    val aiSummary: String? = null,
    val type: PlaceType,
    val photoMetadata: PhotoMetadata? = null,
    val userRatingsTotal: Int = 0,
    val rating: Double = 0.0
)

enum class PlaceType {
    MUSEUM, PARK, TOURIST_ATTRACTION, HISTORIC_SITE, RESTAURANT, STORE, OTHER
}
