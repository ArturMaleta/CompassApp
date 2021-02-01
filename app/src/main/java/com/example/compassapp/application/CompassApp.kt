package com.example.compassapp.application

import android.app.Application
import com.example.compassapp.koin.appModule
import com.example.compassapp.koin.viewmodelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CompassApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CompassApp)
            modules(listOf(appModule, viewmodelModule))
        }
    }
}