package com.kiryha.noting

import android.app.Application
import com.kiryha.noting.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NotingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NotingApplication)
            modules(appModules)
        }
    }
}