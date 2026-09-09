package com.gunkel.android.drift.feature.map.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gunkel.android.drift.feature.map.data.local.DriftDatabase
import com.gunkel.android.drift.feature.map.data.local.entities.IgnoredPlaceEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IgnoredPlaceDaoTest {

    private lateinit var database: DriftDatabase
    private lateinit var dao: IgnoredPlaceDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DriftDatabase::class.java
        ).build()
        dao = database.ignoredPlaceDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetAll_shouldReturnPlacesSortedByTimestampDesc() = runTest {
        val place1 = IgnoredPlaceEntity("1", "Place 1", null, timestamp = 100)
        val place2 = IgnoredPlaceEntity("2", "Place 2", null, timestamp = 200)
        
        dao.insertIgnoredPlace(place1)
        dao.insertIgnoredPlace(place2)
        
        val result = dao.getAllIgnoredPlaces().first()
        
        assertEquals(2, result.size)
        assertEquals("2", result[0].id)
        assertEquals("1", result[1].id)
    }

    @Test
    fun deleteById_shouldRemovePlace() = runTest {
        val place = IgnoredPlaceEntity("1", "Place 1", null)
        dao.insertIgnoredPlace(place)
        
        dao.deleteById("1")
        
        val result = dao.getAllIgnoredPlaces().first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun getIgnoredIds_shouldReturnListOfIds() = runTest {
        dao.insertIgnoredPlace(IgnoredPlaceEntity("1", "Place 1", null))
        dao.insertIgnoredPlace(IgnoredPlaceEntity("2", "Place 2", null))
        
        val ids = dao.getIgnoredIds()
        
        assertEquals(2, ids.size)
        assertTrue(ids.contains("1"))
        assertTrue(ids.contains("2"))
    }
}
