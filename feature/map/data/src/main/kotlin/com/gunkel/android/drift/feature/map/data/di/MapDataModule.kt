package com.gunkel.android.drift.feature.map.data.di

import androidx.room.Room
import com.google.android.libraries.places.api.Places
import com.gunkel.android.drift.core.network.api.DirectionsApi
import com.gunkel.android.drift.core.network.api.PlacesV1Api
import com.gunkel.android.drift.feature.map.data.local.DriftDatabase
import com.gunkel.android.drift.feature.map.data.repositories.DriftRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val mapDataModule = module {
    single {
        Room.databaseBuilder(androidContext(), DriftDatabase::class.java, "drift_database")
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<DriftDatabase>().ignoredPlaceDao() }
    single { get<DriftDatabase>().categorySettingDao() }
    
    single { get<Retrofit>().create(DirectionsApi::class.java) }
    single { get<Retrofit>().create(PlacesV1Api::class.java) }
    single { Places.createClient(androidContext()) }

    single { DriftRepository(get(), get(), get(), get(named("MAPS_API_KEY")), get(), get()) }
}
