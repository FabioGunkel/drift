package com.gunkel.android.drift.core.user.di

import android.content.Context
import androidx.startup.Initializer
import com.gunkel.android.drift.core.common.di.CoreKoinInitializer
import org.koin.core.context.loadKoinModules

class UserInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        loadKoinModules(userModule)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(CoreKoinInitializer::class.java)
    }
}
