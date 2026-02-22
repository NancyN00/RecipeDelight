package com.nancy.recipedelight.ui.viewmodel

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
import java.io.IOException

class HomeViewModel(private val repository: MealRepository) : ViewModel() {

    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    var randomMeal by mutableStateOf<Meal?>(null)
        private set

    var searchResults by mutableStateOf<List<Meal>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    var searchError by mutableStateOf<String?>(null)
        private set

    var networkError by mutableStateOf<String?>(null)
        private set

    val bookmarkedMeals: StateFlow<List<Meal>> = repository.getBookmarkedMeals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val isRandomMealBookmarked: StateFlow<Boolean> = snapshotFlow { randomMeal }
        .flatMapLatest { meal ->
            if (meal == null) flowOf(false)
            else repository.isMealBookmarked(meal.id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        viewModelScope.launch {
            fetchCategories()
            fetchRandomMeal()
        }
    }

    private suspend fun fetchCategories() {
        try {
            categories = repository.getCategories()
            networkError = null
        } catch (e: IOException) {
            networkError = "No internet connection. Please try again."
        } catch (e: Exception) {
            networkError = "Failed to load categories."
        }
    }

    private suspend fun fetchRandomMeal() {
        try {
            randomMeal = repository.getRandomMeal()
            networkError = null
        } catch (e: IOException) {
            networkError = "No internet connection. Please try again."
        } catch (e: Exception) {
            networkError = "Failed to load random meal."
        }
    }

    fun toggleBookmark(meal: Meal) {
        viewModelScope.launch {
            repository.toggleBookmark(meal)
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        if (newQuery.isEmpty()) {
            searchError = null
        }
    }

    fun performSearch() {
        if (searchQuery.isBlank()) return

        viewModelScope.launch {
            try {
                val results = repository.searchMeals(searchQuery)
                if (results.isEmpty()) {
                    searchResults = emptyList()
                    searchError = "No recipes found for '$searchQuery'"
                } else {
                    searchResults = results
                    searchError = null
                }
                networkError = null
            } catch (e: IOException) {
                networkError = "No internet connection. Please try again."
            } catch (e: Exception) {
                networkError = "Failed to perform search."
            }
        }
    }

    fun clearSearch() {
        searchQuery = ""
        searchResults = emptyList()
        searchError = null
        networkError = null
    }
}