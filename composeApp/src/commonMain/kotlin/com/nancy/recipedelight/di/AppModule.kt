package com.nancy.recipedelight.di

import com.nancy.recipedelight.data.repository.MealRepoImpl
import com.nancy.recipedelight.domain.repositories.MealRepository
import io.ktor.client.*
import org.koin.dsl.module

val appModule = module {
    single { HttpClientFactory.create() }
    single<MealRepository> { MealRepoImpl(get()) }
}
