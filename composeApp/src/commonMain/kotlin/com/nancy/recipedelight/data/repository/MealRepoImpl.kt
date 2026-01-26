package com.nancy.recipedelight.data.repository

import com.nancy.recipedelight.data.remote.*
import com.nancy.recipedelight.domain.models.*
import com.nancy.recipedelight.domain.repositories.MealRepository
import io.ktor.client.HttpClient

class MealRepoImpl(private val client: HttpClient) : MealRepository {

    override suspend fun getRandomMeal(): Meal {
        val response = MealApi.getRandomMeal(client)
        val mealDto = response.meals?.firstOrNull()
        return mealDto?.toDomain() ?: throw IllegalStateException("No random meal found")
    }

    override suspend fun getCategories(): List<Category> {
        val response = MealApi.getCategories(client)
        return response.categories?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getMealsByCategory(categoryName: String): List<MealSummary> {
        val response = MealApi.getMealsByCategory(client, categoryName)
        return response.meals?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getMealDetails(mealId: String): Meal {
        val response = MealApi.getMealDetails(client, mealId)
        val mealDto = response.meals?.firstOrNull()
        return mealDto?.toDomain() ?: throw IllegalStateException("Meal not found for ID: $mealId")
    }

    suspend fun toggleBookmark(meal: Meal) {
        val isBookmarked = queries.selectBookmarkById(meal.id).executeAsOneOrNull() != null
        if (isBookmarked) {
            queries.deleteBookmark(meal.id)
        } else {
            queries.insertBookmark(
                BookmarkEntity(
                    id = meal.id,
                    name = meal.name,
                    category = meal.category,
                    area = meal.area,
                    instructions = meal.instructions,
                    thumb = meal.thumb,
                    tags = meal.tags,
                    youtube = meal.youtube,
                    ingredients = meal.ingredients
                )
            )
        }
    }
}

/** --- DTO to Domain mapping --- */

private fun MealDto.toDomain(): Meal {
    val ingredients = listOfNotNull(
        strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
        strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
        strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
        strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20
    ).filter { it.isNotBlank() }

    val measures = listOfNotNull(
        strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5,
        strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10,
        strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15,
        strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20
    ).filter { it.isNotBlank() }

    return Meal(
        id = idMeal,
        name = strMeal,
        category = strCategory,
        area = strArea,
        instructions = strInstructions,
        thumb = strMealThumb,
        tags = strTags?.split(",") ?: emptyList(),
        youtube = strYoutube,
        ingredients = ingredients.zip(measures).map { (ing, measure) -> "$ing - $measure" }
    )
}

private fun CategoryDto.toDomain(): Category = Category(
    id = idCategory,
    name = strCategory,
    thumb = strCategoryThumb,
    description = strCategoryDescription
)

private fun MealSummaryDto.toDomain(): MealSummary = MealSummary(
    id = idMeal,
    name = strMeal,
    thumb = strMealThumb
)
