package com.gunkel.android.drift.feature.map.data.di

import androidx.room.Room
import coil3.ImageLoader
import com.gunkel.android.drift.feature.map.data.local.DriftDatabase
import com.gunkel.android.drift.feature.map.data.repositories.DriftRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val mapDataModule = module {
    single {
        Room.databaseBuilder(androidContext(), DriftDatabase::class.java, "drift_database")
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<DriftDatabase>().ignoredPlaceDao() }
    single { DriftRepository(get(), get(), get(), get(named("MAPS_API_KEY")), get()) }
}
