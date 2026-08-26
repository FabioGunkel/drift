package com.gunkel.android.drift.feature.map.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gunkel.android.drift.feature.map.data.local.dao.IgnoredPlaceDao
import com.gunkel.android.drift.feature.map.data.local.entities.IgnoredPlaceEntity

@Database(entities = [IgnoredPlaceEntity::class], version = 1, exportSchema = false)
abstract class DriftDatabase : RoomDatabase() {
    abstract fun ignoredPlaceDao(): IgnoredPlaceDao
}
