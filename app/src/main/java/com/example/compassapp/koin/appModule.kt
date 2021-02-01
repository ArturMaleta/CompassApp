package com.example.compassapp.koin

import com.example.compassapp.util.Compass
import com.example.compassapp.viewmodel.CompassAppViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { Compass(androidContext()) }
}

val viewmodelModule = module {
    viewModel { CompassAppViewModel() }
}