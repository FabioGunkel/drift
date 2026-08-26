package com.gunkel.android.drift.feature.map.ui.di

import com.gunkel.android.drift.feature.map.ui.viewmodels.IgnoredPlacesViewModel
import com.gunkel.android.drift.feature.map.ui.viewmodels.MapViewModel
import com.gunkel.android.drift.feature.map.ui.viewmodels.PlaceDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mapUiModule = module {
    viewModel { MapViewModel(get(), get()) }
    viewModel { IgnoredPlacesViewModel(get(), get()) }
    viewModel { PlaceDetailsViewModel(get(), get()) }
}
