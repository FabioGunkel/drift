package com.gunkel.android.drift.feature.map.ui.di

import com.gunkel.android.drift.feature.map.ui.viewmodels.MapViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mapUiModule = module {
    viewModel { MapViewModel(get()) }
}
