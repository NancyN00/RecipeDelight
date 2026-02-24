package com.nancy.recipedelight

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.nancy.recipedelight.data.local.RecipeDatabase
import com.nancy.recipedelight.voice.AndroidVoiceRecognizer
import com.nancy.recipedelight.voice.VoiceRecognizer
import org.koin.android.ext.koin.androidContext
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


}