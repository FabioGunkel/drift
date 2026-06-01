package com.gunkel.android.drift.di

import com.gunkel.android.drift.feature.map.data.repositories.MapRepository
import com.gunkel.android.drift.feature.map.domain.usecases.GetNearbyPlacesUseCase

object AppContainer {
    lateinit var mapRepository: MapRepository
    lateinit var getNearbyPlacesUseCase: GetNearbyPlacesUseCase
}
