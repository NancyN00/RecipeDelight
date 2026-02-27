package com.nancy.recipedelight.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.nancy.recipedelight.data.local.RecipeDatabase
import com.nancy.recipedelight.voice.AndroidVoiceRecognizer
import com.nancy.recipedelight.voice.VoiceRecognizer
import com.nancy.recipedelight.workmanager.MealWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module


val androidModule = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = RecipeDatabase.Schema,
            context = get(),
            name = "recipes.db"
        )
    }

    //tells koin how to instantiate voice recognizer

    single<VoiceRecognizer> { AndroidVoiceRecognizer(androidContext()) }

    worker { params ->
        MealWorker(
            context = get(),            // Gets Application Context from Koin
            workerParams = params.get(), // Gets WorkerParameters from WorkManager
            repository = get()          // Injects the MealRepository
        )
    }


    // This tells Koin to handle the creation of MealWorker. It will automatically 'get()' the repository from your shared module
//   workerOf(::MealWorker)

}