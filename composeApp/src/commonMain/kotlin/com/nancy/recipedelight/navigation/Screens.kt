package com.nancy.recipedelight.navigation

sealed class Screen(val route: String) {

    // Bottom bar
    object Home : Screen("home")
    object Bookmarks : Screen("bookmarks")

    // Category screen
    object CategoryMeals : Screen("category/{categoryName}") {
        fun createRoute(categoryName: String) = "category/$categoryName"
    }

    // Meal details screen
    object MealDetails : Screen("meal/{mealId}") {
        fun createRoute(mealId: String) = "meal/$mealId"
    }
}
