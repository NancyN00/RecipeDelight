package com.nancy.recipedelight.di


import com.nancy.recipedelight.ui.viewmodel.HomeViewModel
import com.nancy.recipedelight.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(repository = get()) }

    viewModel { SettingsViewModel(repository = get()) }
}