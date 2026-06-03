package com.gunkel.android.drift.feature.map.data.di

import coil3.ImageLoader
import com.gunkel.android.drift.feature.map.data.repositories.DriftRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val mapDataModule = module {
    single { ImageLoader(androidContext()) }
    single { DriftRepository(get(), get(), get(), androidContext(), get(named("MAPS_API_KEY"))) }
}
