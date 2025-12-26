package com.nancy.recipedelight.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nancy.recipedelight.domain.models.Category
import com.nancy.recipedelight.domain.models.Meal
import com.nancy.recipedelight.domain.repositories.MealRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: MealRepository) : ViewModel() {

    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    var randomMeal by mutableStateOf<Meal?>(null)
        private set

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
}
