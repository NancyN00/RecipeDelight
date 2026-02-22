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

    var isLoadingCategories by mutableStateOf(false)
        private set
    var isLoadingRandomMeal by mutableStateOf(false)
        private set
    var isSearching by mutableStateOf(false)
        private set

    val bookmarkedMeals: StateFlow<List<Meal>> = repository.getBookmarkedMeals()
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val isRandomMealBookmarked: StateFlow<Boolean> = snapshotFlow { randomMeal }
        .flatMapLatest { meal ->
            if (meal == null) flowOf(false)
            else repository.isMealBookmarked(meal.id)
        }.stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = false)

    init {
        fetchAll()
    }

    private fun fetchAll() {
        viewModelScope.launch { fetchCategories() }
        viewModelScope.launch { fetchRandomMeal() }
    }

    private suspend fun fetchCategories() {
        isLoadingCategories = true
        try {
            categories = repository.getCategories()
            networkError = null
        } catch (e: IOException) {
            networkError = "No internet. Showing cached data."
        } catch (e: Exception) {
            networkError = "Failed to load categories."
        } finally {
            isLoadingCategories = false
        }
    }

    private suspend fun fetchRandomMeal() {
        isLoadingRandomMeal = true
        try {
            randomMeal = repository.getRandomMeal()
            networkError = null
        } catch (e: IOException) {
            networkError = "No internet. Showing cached data."
        } catch (e: Exception) {
            networkError = "Failed to load random meal."
        } finally {
            isLoadingRandomMeal = false
        }
    }

    fun toggleBookmark(meal: Meal) {
        viewModelScope.launch { repository.toggleBookmark(meal) }
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        if (newQuery.isEmpty()) searchError = null
    }

    fun performSearch() {
        if (searchQuery.isBlank()) return
        viewModelScope.launch {
            isSearching = true
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
                networkError = "No internet. Showing cached results."
            } catch (e: Exception) {
                networkError = "Failed to search meals."
            } finally {
                isSearching = false
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