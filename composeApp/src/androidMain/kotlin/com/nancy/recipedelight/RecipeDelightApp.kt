package com.nancy.recipedelight

import android.app.Application
import com.nancy.recipedelight.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RecipeDelightApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RecipeDelightApp)
            modules(appModule)
        }
    }
}
