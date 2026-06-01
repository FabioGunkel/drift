package com.gunkel.android.drift.feature.map.domain.di

import com.gunkel.android.drift.feature.map.domain.usecases.CalculateDriftPathUseCase
import com.gunkel.android.drift.feature.map.domain.usecases.GetDriftWalkingPathUseCase
import org.koin.dsl.module

val mapDomainModule = module {
    factory { CalculateDriftPathUseCase(get()) }
    factory { GetDriftWalkingPathUseCase(get(), get()) }
}
