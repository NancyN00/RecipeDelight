package com.nancy.recipedelight.di

import com.nancy.recipedelight.BuildConfig
import com.nancy.recipedelight.data.local.RecipeDatabase
import com.nancy.recipedelight.data.remote.GeminiApiService
import com.nancy.recipedelight.domain.repositories.GeminiRepository
import com.nancy.recipedelight.repo.GeminiApiServiceImpl
import com.nancy.recipedelight.ui.viewmodel.GeminiViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val geminiModule = module {
    // Android-only API service (needs BuildConfig)
    single<GeminiApiService> { GeminiApiServiceImpl(BuildConfig.GEMINI_API_KEY) }

    single { get<RecipeDatabase>().chatEntityQueries }

    single { GeminiRepository(get(), get()) }

    viewModel { GeminiViewModel(get()) }
}
