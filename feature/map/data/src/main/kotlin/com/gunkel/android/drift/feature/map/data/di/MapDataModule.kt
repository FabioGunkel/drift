package com.gunkel.android.drift.feature.map.data.di

import com.gunkel.android.drift.feature.map.data.repositories.DriftRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val mapDataModule = module {
    single { DriftRepository(get(), get(), get(named("MAPS_API_KEY"))) }
}
