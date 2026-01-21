package com.nancy.recipedelight.di


import com.nancy.recipedelight.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        HomeViewModel(
            repository = get()
        )
    }
}
