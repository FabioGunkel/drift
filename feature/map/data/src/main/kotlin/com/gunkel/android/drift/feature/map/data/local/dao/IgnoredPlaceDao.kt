package com.gunkel.android.drift.feature.map.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gunkel.android.drift.feature.map.data.local.entities.IgnoredPlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IgnoredPlaceDao {
    @Query("SELECT * FROM ignored_places ORDER BY timestamp DESC")
    fun getAllIgnoredPlaces(): Flow<List<IgnoredPlaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIgnoredPlace(place: IgnoredPlaceEntity)

    @Query("DELETE FROM ignored_places WHERE id = :id")
    suspend fun deleteById(id: String)
    
    @Query("SELECT id FROM ignored_places")
    suspend fun getIgnoredIds(): List<String>
}
