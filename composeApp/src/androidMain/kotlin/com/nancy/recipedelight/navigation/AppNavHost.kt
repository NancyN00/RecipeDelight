package com.nancy.recipedelight.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nancy.recipedelight.domain.repositories.MealRepository
import com.nancy.recipedelight.ui.bookmark.BookMarksScreen
import com.nancy.recipedelight.ui.home.HomeScreen
import com.nancy.recipedelight.ui.home.HomeViewModel
import com.nancy.recipedelight.ui.home.categories.CategoryScreen
import com.nancy.recipedelight.ui.home.details.MealDetailsScreen
import com.nancy.recipedelight.ui.profile.ProfileScreen
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val repository: MealRepository = get()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // Home screen
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = koinViewModel()
            HomeScreen(
                viewModel = homeViewModel,
                onCategoryClick = { categoryName ->
                    navController.navigate(Screen.CategoryMeals.createRoute(categoryName))
                },
//                onMealClick = { mealId ->
//                    navController.navigate(Screen.MealDetails.createRoute(mealId))
//                }
            )
        }

        // Bookmarks screen
        composable(Screen.Bookmarks.route) {
            BookMarksScreen()
        }

        // Account/Profile screen
        composable(Screen.Account.route) {
            ProfileScreen()
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
            Screen.MealDetails.route,
            arguments = listOf(navArgument("mealId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId") ?: ""
            MealDetailsScreen(
                mealId = mealId,
                repository = repository,
                onBack = { navController.popBackStack() },
                onBookmarkClick = { meal ->
                    // TODO: implement add/remove from favorite
                }
            )
        }
    }
}
