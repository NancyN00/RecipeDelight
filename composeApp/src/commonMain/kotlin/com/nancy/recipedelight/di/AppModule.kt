package com.nancy.recipedelight.di

import com.nancy.recipedelight.data.db.listAdapter
import com.nancy.recipedelight.data.local.AppSettingsQueries
import com.nancy.recipedelight.data.local.MealEntity
import com.nancy.recipedelight.data.local.RecipeDatabase
import com.nancy.recipedelight.data.repository.AppSettingsRepositoryImpl
import com.nancy.recipedelight.data.repository.MealRepoImpl
import com.nancy.recipedelight.domain.repositories.AppSettingsRepository
import com.nancy.recipedelight.domain.repositories.MealRepository
import org.koin.dsl.module

val appModule = module {
    single { HttpClientFactory.create() }

    single {
        RecipeDatabase(
            driver = get(),
            MealEntityAdapter = MealEntity.Adapter(
                tagsAdapter = listAdapter,
                ingredientsAdapter = listAdapter,
            )
        )
    }

    single { get<RecipeDatabase>().mealQueries }
    single<AppSettingsQueries> { get<RecipeDatabase>().appSettingsQueries }

    single<MealRepository> { MealRepoImpl(get(), get()) }

    single<AppSettingsRepository> { AppSettingsRepositoryImpl(get()) }
}