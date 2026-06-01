package com.gunkel.android.drift.feature.map.data.di

import com.gunkel.android.drift.feature.map.data.repositories.MapRepositoryImpl
import com.gunkel.android.drift.feature.map.domain.repositories.MapRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapDataModule {

    @Provides
    @Singleton
    fun provideMapRepository(): MapRepository {
        return MapRepositoryImpl()
    }
}
