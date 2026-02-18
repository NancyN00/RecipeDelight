package com.nancy.recipedelight.sql

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.nancy.recipedelight.data.local.RecipeDatabase

class DriverFactory(private val context: Context) {
    fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = RecipeDatabase.Companion.Schema,
            context = context,
            name = "recipe_delight.db"
        )
    }
}