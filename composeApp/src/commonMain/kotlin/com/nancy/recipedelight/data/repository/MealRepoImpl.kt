package com.nancy.recipedelight.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.nancy.recipedelight.data.local.BookmarkEntity
import com.nancy.recipedelight.data.local.MealQueries
import com.nancy.recipedelight.data.remote.*
import com.nancy.recipedelight.domain.models.*
import com.nancy.recipedelight.domain.repositories.MealRepository
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MealRepoImpl(
    private val client: HttpClient,
    private val queries: MealQueries
) : MealRepository {

    //API IMPLEMENTATION

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

    //DATABASE IMPLEMENTATION

    override fun getBookmarkedMeals(): Flow<List<Meal>> {
        return queries.selectAllBookmarks()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override fun isMealBookmarked(id: String): Flow<Boolean> {
        return queries.isBookmarked(id)
            .asFlow()
            .mapToOne(Dispatchers.Default)
            .map { count -> count > 0 }
    }


    override suspend fun toggleBookmark(meal: Meal) {
        // Check if it exists using the count query
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

    override suspend fun searchMeals(query: String): List<Meal> {
        return try {
            // 1. Use MealApi or client.get directly.
            // Based on your code, let's add a search method to your MealApi or call via client:
            val response: MealResponse = MealApi.searchMeals(client, query)

            // 2. Map the List<MealDto> to List<Meal> using your private toDomain() extension
            response.meals?.map { it.toDomain() } ?: emptyList()

        } catch (e: Exception) {
            // Return empty list on failure so the ViewModel handles the "Not Found" state
            emptyList()
        }
    }
}

//Domain mapping for the Database
fun BookmarkEntity.toDomain(): Meal {
    return Meal(
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
}

/** --- DTO to Domain mapping
 * this toDomain() extension functions are for Network DTOs--- */

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
