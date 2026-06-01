package com.gunkel.android.drift.feature.map.ui.di

import com.gunkel.android.drift.feature.map.domain.repositories.MapRepository
import com.gunkel.android.drift.feature.map.domain.usecases.GetNearbyPlacesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object MapDomainModule {

    @Provides
    @ViewModelScoped
    fun provideGetNearbyPlacesUseCase(
        repository: MapRepository
    ): GetNearbyPlacesUseCase {
        return GetNearbyPlacesUseCase(repository)
    }
}
