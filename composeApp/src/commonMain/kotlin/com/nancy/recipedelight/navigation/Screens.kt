package com.nancy.recipedelight.navigation

sealed class Screen(val route: String) {

    //splash
    object Splash : Screen("splash")

    // Bottom bar
    object Home : Screen("home")
    object Bookmarks : Screen("bookmarks")

    object ChefAI : Screen("chefai")

    // Category screen
    object CategoryMeals : Screen("category/{categoryName}") {
        fun createRoute(categoryName: String) = "category/$categoryName"
    }

    // Meal details screen
    object MealDetails : Screen("meal/{mealId}") {
        fun createRoute(mealId: String) = "meal/$mealId"
    }
}
