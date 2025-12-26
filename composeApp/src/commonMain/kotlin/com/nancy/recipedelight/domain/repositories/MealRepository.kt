package com.nancy.recipedelight.domain.repositories

import com.nancy.recipedelight.domain.models.Category
import com.nancy.recipedelight.domain.models.Meal
import com.nancy.recipedelight.domain.models.MealSummary

interface MealRepository {

    suspend fun getRandomMeal(): Meal

    suspend fun getCategories(): List<Category>

    suspend fun getMealsByCategory(categoryName: String): List<MealSummary>

    suspend fun getMealDetails(mealId: String): Meal
}
