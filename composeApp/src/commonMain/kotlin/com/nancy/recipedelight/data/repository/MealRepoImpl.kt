package com.nancy.recipedelight.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.nancy.recipedelight.data.local.BookmarkEntity
import com.nancy.recipedelight.data.local.MealQueries
import com.nancy.recipedelight.data.remote.CategoriesResponse
import com.nancy.recipedelight.data.remote.CategoryDto
import com.nancy.recipedelight.data.remote.MealApi
import com.nancy.recipedelight.data.remote.MealDto
import com.nancy.recipedelight.data.remote.MealResponse
import com.nancy.recipedelight.data.remote.MealSummaryDto
import com.nancy.recipedelight.data.remote.MealsByCategoryResponse
import com.nancy.recipedelight.domain.models.Category
import com.nancy.recipedelight.domain.models.Meal
import com.nancy.recipedelight.domain.models.MealSummary
import com.nancy.recipedelight.domain.repositories.MealRepository
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MealRepoImpl(
    private val client: HttpClient,
    private val queries: MealQueries
) : MealRepository {

    /** NETWORK IMPLEMENTATION */

    override suspend fun getRandomMeal(): Meal? {
        return try {
            val response = MealApi.getRandomMeal(client)
            response.meals?.firstOrNull()?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getCategories(): List<Category> {
        return try {
            val response: CategoriesResponse = MealApi.getCategories(client)
            response.categories?.map { it.toDomain() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMealsByCategory(categoryName: String): List<MealSummary> {
        return try {
            val response: MealsByCategoryResponse = MealApi.getMealsByCategory(client, categoryName)
            response.meals?.map { it.toDomain() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMealDetails(mealId: String): Meal? {
        return try {
            val response = MealApi.getMealDetails(client, mealId)
            response.meals?.firstOrNull()?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun searchMeals(query: String): List<Meal> {
        return try {
            val response: MealResponse = MealApi.searchMeals(client, query)
            response.meals?.map { it.toDomain() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** DATABASE IMPLEMENTATION */

    override fun getBookmarkedMeals(): Flow<List<Meal>> {
        return queries.selectAllBookmarks()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun isMealBookmarked(id: String): Flow<Boolean> {
        return queries.isBookmarked(id)
            .asFlow()
            .mapToOne(Dispatchers.Default)
            .map { count -> count > 0 }
    }

    override suspend fun toggleBookmark(meal: Meal) {
        val exists = queries.isBookmarked(meal.id).executeAsOne() > 0
        if (exists) {
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

/** DATABASE MAPPING */

fun BookmarkEntity.toDomain(): Meal = Meal(
    id = id,
    name = name,
    category = category,
    area = area,
    instructions = instructions,
    thumb = thumb,
    tags = tags ?: emptyList(),
    youtube = youtube,
    ingredients = ingredients ?: emptyList()
)

/**  NETWORK DTO TO DOMAIN */
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