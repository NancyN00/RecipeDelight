package com.nancy.recipedelight.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nancy.recipedelight.domain.models.Category
import com.nancy.recipedelight.domain.models.Meal
import com.nancy.recipedelight.domain.repositories.MealRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: MealRepository) : ViewModel() {

    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    var randomMeal by mutableStateOf<Meal?>(null)
        private set

    // observe all bookmarked meals for a "Favorites" row on Home
    val bookmarkedMeals: StateFlow<List<Meal>> = repository.getBookmarkedMeals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Observe if the CURRENT random meal is bookmarked
    // This updates automatically whenever the randomMeal changes OR the DB changes
    // use flatMapLatest so that if the random meal changes,
    // stop watching the old ID and start watching the new one.
    @OptIn(ExperimentalCoroutinesApi::class)
    val isRandomMealBookmarked: StateFlow<Boolean> = snapshotFlow { randomMeal }
        .flatMapLatest { meal ->
            if (meal == null) flowOf(false)
            else repository.isMealBookmarked(meal.id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = false
        )


    init {
        viewModelScope.launch {
            fetchCategories()
            fetchRandomMeal()
        }
    }

    private suspend fun fetchCategories() {
        categories = repository.getCategories()
    }

    private suspend fun fetchRandomMeal() {
        randomMeal = repository.getRandomMeal()
    }

    // Toggle action for the Random Meal or Favorites list
    fun toggleBookmark(meal: Meal) {
        viewModelScope.launch {
            repository.toggleBookmark(meal)
        }
    }

}