package com.nancy.recipedelight.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nancy.recipedelight.domain.repositories.MealRepository
import com.nancy.recipedelight.ui.ChefAiScreen
import com.nancy.recipedelight.ui.home.HomeScreen
import com.nancy.recipedelight.ui.HomeViewModel
import com.nancy.recipedelight.ui.bookmark.BookmarkScreen
import com.nancy.recipedelight.ui.home.categories.CategoryScreen
import com.nancy.recipedelight.ui.home.details.MealDetailsScreen
import com.nancy.recipedelight.ui.splash.SplashScreen
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val repository: MealRepository = get()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {

        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(onNavigateToHome = {
                navController.navigate(Screen.Home.route) {
                    // Remove splash from backstack so back button doesn't go back to it
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }


        // Home screen
        composable(Screen.Home.route) {
            HomeScreen(
                onCategoryClick = { categoryName ->
                    navController.navigate(Screen.CategoryMeals.createRoute(categoryName))
                },
            )
        }

        // Bookmarks screen
        composable(Screen.Bookmarks.route) {
            BookmarkScreen(
                onMealClick = { id ->
                    // Use the helper from the Screen class to ensure the string is perfect
                    navController.navigate(Screen.MealDetails.createRoute(id))
                })
        }

        // Category screen
        composable(
            Screen.CategoryMeals.route,
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            CategoryScreen(
                categoryName = categoryName,
                repository = repository,
                onMealClick = { mealId ->
                    navController.navigate(Screen.MealDetails.createRoute(mealId))
                }
            )
        }

        // Meal details screen
        composable(
            route = Screen.MealDetails.route,
            arguments = listOf(navArgument("mealId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId") ?: ""
            MealDetailsScreen(
                mealId = mealId,
                repository = repository,
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.ChefAI.route) {
            ChefAiScreen()
        }

    }
}
