package com.nancy.recipedelight.di

import com.nancy.recipedelight.data.db.listAdapter
import com.nancy.recipedelight.data.local.AppSettingsQueries
import com.nancy.recipedelight.data.local.BookmarkEntity
import com.nancy.recipedelight.data.local.RecipeDatabase
import com.nancy.recipedelight.data.repository.AppSettingsRepositoryImpl
import com.nancy.recipedelight.data.repository.MealRepoImpl
import com.nancy.recipedelight.domain.repositories.AppSettingsRepository
import com.nancy.recipedelight.domain.repositories.MealRepository
import org.koin.dsl.module

val appModule = module {
    single { HttpClientFactory.create() }

    // Provide the Database
    single {
        RecipeDatabase(
            driver = get(),
            BookmarkEntityAdapter = BookmarkEntity.Adapter(
                tagsAdapter = listAdapter,
                ingredientsAdapter = listAdapter
            )
        )
    }

    // Provide Queries
    single<AppSettingsQueries> { get<RecipeDatabase>().appSettingsQueries }
    single { get<RecipeDatabase>().mealQueries }

    // Repositories
    single<MealRepository> { MealRepoImpl(get(), get()) }
    single<AppSettingsRepository> { AppSettingsRepositoryImpl(get()) }
}