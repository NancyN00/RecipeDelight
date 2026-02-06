package com.nancy.recipedelight.di

import com.nancy.recipedelight.data.db.listAdapter
import com.nancy.recipedelight.data.local.BookmarkEntity
import com.nancy.recipedelight.data.local.RecipeDatabase
import com.nancy.recipedelight.data.repository.MealRepoImpl
import com.nancy.recipedelight.domain.repositories.MealRepository
import org.koin.dsl.module

val appModule = module {
    single { HttpClientFactory.create() }

    // specific Queries object from the Database
    single { get<RecipeDatabase>().mealQueries }

    // both dependencies to the Repository
    // Koin will automatically match the HttpClient and MealQueries
    single<MealRepository> { MealRepoImpl(get(), get()) }

    single {
        RecipeDatabase(
            driver = get(), // Provided by the platform module (Android/iOS)
            BookmarkEntityAdapter = BookmarkEntity.Adapter(
                tagsAdapter = listAdapter,
                ingredientsAdapter = listAdapter
            )
        )
    }
}
