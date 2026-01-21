package com.nancy.recipedelight.ui.home.categories

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.nancy.recipedelight.domain.models.MealSummary
import com.nancy.recipedelight.domain.repositories.MealRepository
import kotlinx.coroutines.launch

@Composable
fun CategoryScreen(
    categoryName: String,
    repository: MealRepository,
    onMealClick: (mealId: String) -> Unit
) {
    var meals by remember { mutableStateOf<List<MealSummary>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch meals when screen loads
    LaunchedEffect(categoryName) {
        coroutineScope.launch {
            meals = repository.getMealsByCategory(categoryName)
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(meals) { meal ->
            MealCard(meal) {
                onMealClick(meal.id)
            }
        }
    }
}

@Composable
fun MealCard(meal: MealSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter(meal.thumb),
                contentDescription = meal.name,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
