package com.nancy.recipedelight.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nancy.recipedelight.domain.repositories.MealRepository
import com.nancy.recipedelight.ui.bookmark.BookmarkScreen
import com.nancy.recipedelight.ui.chefai.ChefAiScreen
import com.nancy.recipedelight.ui.home.HomeScreen
import com.nancy.recipedelight.ui.home.categories.CategoryScreen
import com.nancy.recipedelight.ui.home.details.MealDetailsScreen
import com.nancy.recipedelight.ui.viewmodel.GeminiViewModel
import com.nancy.recipedelight.voice.VoiceRecognizer
import org.koin.androidx.compose.get

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val repository: MealRepository = get()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // Home screen
        composable(Screen.Home.route) {
            HomeScreen(
                onCategoryClick = { categoryName ->
                    navController.navigate(Screen.CategoryMeals.createRoute(categoryName))
                },
                onMealClick = { id ->
                    // helper from the Screen class ensure the string is perfect
                    navController.navigate(Screen.MealDetails.createRoute(id))
                }
            )
        }

        // Bookmarks screen
        composable(Screen.Bookmarks.route) {
            BookmarkScreen(
                onMealClick = { id ->
                    navController.navigate(Screen.MealDetails.createRoute(id))
                })
        }

        //ChefAI screen
        composable(Screen.ChefAI.route){
            ChefAiScreen()
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
                },
                onBackClick = {
                    navController.popBackStack()
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
    }
}
