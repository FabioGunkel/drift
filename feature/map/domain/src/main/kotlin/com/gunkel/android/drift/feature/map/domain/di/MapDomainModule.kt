package com.gunkel.android.drift.feature.map.domain.di

import com.gunkel.android.drift.feature.map.domain.usecases.CalculateDriftPathUseCase
import com.gunkel.android.drift.feature.map.domain.usecases.GetDriftWalkingPathUseCase
import com.gunkel.android.drift.feature.map.domain.usecases.GetPlaceAiSummaryUseCase
import com.gunkel.android.drift.feature.map.domain.usecases.ignored.AddIgnoredPlaceUseCase
import com.gunkel.android.drift.feature.map.domain.usecases.ignored.GetIgnoredPlacesUseCase
import com.gunkel.android.drift.feature.map.domain.usecases.ignored.RemoveIgnoredPlaceUseCase
import org.koin.dsl.module

val mapDomainModule = module {
    factory { CalculateDriftPathUseCase(get()) }
    factory { GetDriftWalkingPathUseCase(get(), get()) }
    factory { GetPlaceAiSummaryUseCase(get()) }
    factory { AddIgnoredPlaceUseCase(get()) }
    factory { RemoveIgnoredPlaceUseCase(get()) }
    factory { GetIgnoredPlacesUseCase(get()) }
}
