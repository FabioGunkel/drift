package com.gunkel.android.drift.core.user.di

import com.gunkel.android.drift.core.user.models.UserPreferences
import org.koin.dsl.module

val userModule = module {
    single { UserPreferences() }
}
