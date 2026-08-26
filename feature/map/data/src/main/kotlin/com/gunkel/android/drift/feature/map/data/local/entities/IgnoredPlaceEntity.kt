package com.gunkel.android.drift.feature.map.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ignored_places")
data class IgnoredPlaceEntity(
    @PrimaryKey val id: String,
    val title: String,
    val photoReference: String?,
    val timestamp: Long = System.currentTimeMillis()
)
