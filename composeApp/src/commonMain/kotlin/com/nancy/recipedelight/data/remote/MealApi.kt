package com.nancy.recipedelight.data.remote

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable

object MealApi {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    suspend fun getRandomMeal(client: HttpClient): MealResponse =
        client.get("${BASE_URL}random.php").body()

    suspend fun getCategories(client: HttpClient): CategoriesResponse =
        client.get("${BASE_URL}categories.php").body()

    suspend fun getMealsByCategory(client: HttpClient, categoryName: String): MealsByCategoryResponse =
        client.get("${BASE_URL}filter.php?c=$categoryName").body()

    suspend fun getMealDetails(client: HttpClient, mealId: String): MealResponse =
        client.get("${BASE_URL}lookup.php?i=$mealId").body()

    suspend fun searchMeals(client: HttpClient, query: String): MealResponse =
        client.get("${BASE_URL}search.php?s=$query").body()
}

/** --- Response DTOs --- */

@Serializable
data class MealResponse(val meals: List<MealDto>?)

@Serializable
data class MealDto(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String? = null,
    val strArea: String? = null,
    val strInstructions: String? = null,
    val strMealThumb: String? = null,
    val strTags: String? = null,
    val strYoutube: String? = null,
    val strIngredient1: String? = null,
    val strIngredient2: String? = null,
    val strIngredient3: String? = null,
    val strIngredient4: String? = null,
    val strIngredient5: String? = null,
    val strIngredient6: String? = null,
    val strIngredient7: String? = null,
    val strIngredient8: String? = null,
    val strIngredient9: String? = null,
    val strIngredient10: String? = null,
    val strIngredient11: String? = null,
    val strIngredient12: String? = null,
    val strIngredient13: String? = null,
    val strIngredient14: String? = null,
    val strIngredient15: String? = null,
    val strIngredient16: String? = null,
    val strIngredient17: String? = null,
    val strIngredient18: String? = null,
    val strIngredient19: String? = null,
    val strIngredient20: String? = null,
    val strMeasure1: String? = null,
    val strMeasure2: String? = null,
    val strMeasure3: String? = null,
    val strMeasure4: String? = null,
    val strMeasure5: String? = null,
    val strMeasure6: String? = null,
    val strMeasure7: String? = null,
    val strMeasure8: String? = null,
    val strMeasure9: String? = null,
    val strMeasure10: String? = null,
    val strMeasure11: String? = null,
    val strMeasure12: String? = null,
    val strMeasure13: String? = null,
    val strMeasure14: String? = null,
    val strMeasure15: String? = null,
    val strMeasure16: String? = null,
    val strMeasure17: String? = null,
    val strMeasure18: String? = null,
    val strMeasure19: String? = null,
    val strMeasure20: String? = null
)

@Serializable
data class CategoriesResponse(val categories: List<CategoryDto>?)

@Serializable
data class CategoryDto(
    val idCategory: String,
    val strCategory: String,
    val strCategoryThumb: String? = null,
    val strCategoryDescription: String? = null
)

@Serializable
data class MealsByCategoryResponse(val meals: List<MealSummaryDto>?)

@Serializable
data class MealSummaryDto(
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String? = null
)
