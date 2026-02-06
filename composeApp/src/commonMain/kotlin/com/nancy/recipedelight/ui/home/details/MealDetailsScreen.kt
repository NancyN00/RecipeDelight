package com.nancy.recipedelight.ui.home.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.nancy.recipedelight.domain.models.Meal
import com.nancy.recipedelight.domain.repositories.MealRepository
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailsScreen(
    mealId: String,
    repository: MealRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var meal by remember { mutableStateOf<Meal?>(null) }

    //Observe the bookmark status DIRECTLY from the database Flow
    val isBookmarked by repository.isMealBookmarked(mealId)
        .collectAsState(initial = false)

    LaunchedEffect(mealId) {
        meal = repository.getMealDetails(mealId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = meal?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    meal?.let { currentMeal ->
                        IconButton(onClick = {
                            scope.launch {
                                // 'isBookmarked' state above will update automatically!
                                repository.toggleBookmark(currentMeal)
                            }
                        }) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                // Use PascalCase: Transparent and Green
                                tint = if (isBookmarked) Color.Transparent else Color.Green
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        meal?.let { m ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(m.thumb),
                    contentDescription = m.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Category: ${m.category ?: "-"}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Text(
                    text = "Area: ${m.area ?: "-"}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                m.ingredients.forEach { ingredient ->
                    Text(
                        text = "- $ingredient",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 24.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Instructions",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                Text(
                    text = m.instructions ?: "-",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
