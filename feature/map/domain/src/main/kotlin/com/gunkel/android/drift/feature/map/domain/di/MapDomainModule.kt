package com.gunkel.android.drift.feature.map.domain.di

import com.gunkel.android.drift.feature.map.domain.usecases.GetNearbyPlacesUseCase
import org.koin.dsl.module

val mapDomainModule = module {
    factory { GetNearbyPlacesUseCase(get()) }
}
