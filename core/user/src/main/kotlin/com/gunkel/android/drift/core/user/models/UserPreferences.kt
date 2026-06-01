package com.gunkel.android.drift.core.user.models

data class UserPreferences(
    val radiusMeters: Int = 2000,
    val preferredCategories: List<String> = listOf("museum", "park", "tourist_attraction", "restaurant", "store")
)
