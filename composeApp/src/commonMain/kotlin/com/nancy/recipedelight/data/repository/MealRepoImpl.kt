package com.nancy.recipedelight.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.nancy.recipedelight.data.local.CategoryEntity
import com.nancy.recipedelight.data.local.MealEntity
import com.nancy.recipedelight.data.local.MealQueries
import com.nancy.recipedelight.data.local.MealSummaryEntity
import com.nancy.recipedelight.data.remote.MealApi
import com.nancy.recipedelight.data.remote.MealDto
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

    override suspend fun getRandomMeal(): Meal? {
        return try {
            val meal = MealApi.getRandomMeal(client).meals?.firstOrNull()?.toDomain()
            meal?.let { saveToCache(it) }
            meal
        } catch (e: Exception) {
            queries.selectMealById("random").executeAsOneOrNull()?.toDomain()
        }
    }

    override suspend fun getCategories(): List<Category> {
        return try {
            val list = MealApi.getCategories(client).categories?.map { cat ->
                Category(cat.idCategory, cat.strCategory, cat.strCategoryThumb, cat.strCategoryDescription)
            } ?: emptyList()
            list.forEach { queries.insertCategory(CategoryEntity(it.id, it.name, it.thumb, it.description)) }
            list
        } catch (e: Exception) {
            queries.selectAllCategories().executeAsList().map { Category(it.id, it.name, it.thumb, it.description) }
        }
    }

    override suspend fun getMealsByCategory(categoryName: String): List<MealSummary> {
        return try {
            val list = MealApi.getMealsByCategory(client, categoryName).meals?.map {
                MealSummary(it.idMeal, it.strMeal, it.strMealThumb)
            } ?: emptyList()
            list.forEach { queries.insertMealSummary(MealSummaryEntity(it.id, it.name, it.thumb, categoryName)) }
            list
        } catch (e: Exception) {
            queries.selectSummariesByCategory(categoryName).executeAsList().map { MealSummary(it.id, it.name, it.thumb) }
        }
    }

    override suspend fun getMealDetails(mealId: String): Meal? {
        return try {
            val meal = MealApi.getMealDetails(client, mealId).meals?.firstOrNull()?.toDomain()
            meal?.let { saveToCache(it) }
            meal
        } catch (e: Exception) {
            queries.selectMealById(mealId).executeAsOneOrNull()?.toDomain()
        }
    }

    override suspend fun searchMeals(query: String): List<Meal> {
        return try {
            MealApi.searchMeals(client, query).meals?.map { it.toDomain() } ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    private fun saveToCache(meal: Meal) {
        val isBookmarked = queries.isBookmarked(meal.id).executeAsOne() > 0
        queries.insertMeal(
            meal.id, meal.name, meal.category, meal.area, meal.instructions,
            meal.thumb, meal.tags, meal.youtube, meal.ingredients, isBookmarked
        )
    }

    override fun getBookmarkedMeals(): Flow<List<Meal>> =
        queries.selectAllBookmarks().asFlow().mapToList(Dispatchers.Default).map { list -> list.map { it.toDomain() } }

    override fun isMealBookmarked(id: String): Flow<Boolean> =
        queries.isBookmarked(id).asFlow().mapToOne(Dispatchers.Default).map { it > 0 }

    override suspend fun toggleBookmark(meal: Meal) {
        val exists = queries.isBookmarked(meal.id).executeAsOne() > 0
        queries.updateBookmarkStatus(!exists, meal.id)
    }
}

/** MAPPERS */
fun MealEntity.toDomain() = Meal(
    id = id,
    name = name,
    category = category ?: "",
    area = area ?: "",
    instructions = instructions ?: "",
    thumb = thumb ?: "",
    tags = tags ?: emptyList(),
    youtube = youtube,
    ingredients = ingredients ?: emptyList()
)

private fun MealDto.toDomain(): Meal {
    val ingredientsList = listOfNotNull(strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5, strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10, strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15, strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20).filter { it.isNotBlank() }
    val measures = listOfNotNull(strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5, strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10, strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15, strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20).filter { it.isNotBlank() }

    return Meal(
        id = idMeal, name = strMeal, category = strCategory, area = strArea,
        instructions = strInstructions, thumb = strMealThumb,
        tags = strTags?.split(",") ?: emptyList(), youtube = strYoutube,
        ingredients = ingredientsList.zip(measures).map { "${it.first} - ${it.second}" }
    )
}