package com.nancy.recipedelight.di

import com.nancy.recipedelight.data.db.listAdapter
import com.nancy.recipedelight.data.repository.MealRepoImpl
import com.nancy.recipedelight.domain.repositories.MealRepository
import com.nancy.recipedelight.data.RecipeDatabase
import com.nancy.recipedelight.data.BookmarkEntity
import io.ktor.client.*
import org.koin.dsl.module

val appModule = module {
    single { HttpClientFactory.create() }
    single<MealRepository> { MealRepoImpl(get()) }

    single {
        RecipeDatabase(
            driver = get(),
            BookmarkEntityAdapter = BookmarkEntity.Adapter(
                tagsAdapter = listAdapter,
                ingredientsAdapter = listAdapter
            )
        )
    }
}
