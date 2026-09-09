package com.gunkel.android.drift.feature.map.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gunkel.android.drift.feature.map.data.local.entities.CategorySettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategorySettingDao {
    @Query("SELECT * FROM category_settings")
    fun getAllSettings(): Flow<List<CategorySettingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: CategorySettingEntity)

    @Query("SELECT isEnabled FROM category_settings WHERE categoryName = :categoryName")
    suspend fun isCategoryEnabled(categoryName: String): Boolean?
}
