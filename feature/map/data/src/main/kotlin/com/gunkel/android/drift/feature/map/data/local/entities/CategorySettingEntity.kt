package com.gunkel.android.drift.feature.map.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_settings")
data class CategorySettingEntity(
    @PrimaryKey val categoryName: String,
    val isEnabled: Boolean
)
