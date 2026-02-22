package com.nancy.recipedelight.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.nancy.recipedelight.domain.models.Category
import com.nancy.recipedelight.domain.models.Meal
import com.nancy.recipedelight.ui.search.RecipeSearchBar
import com.nancy.recipedelight.ui.viewmodel.HomeViewModel
import com.nancy.recipedelight.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel(),
    onCategoryClick: (String) -> Unit,
    onMealClick: (String) -> Unit
) {
    val isDark by settingsViewModel.isDarkMode.collectAsState(initial = false)

    val randomMeal = viewModel.randomMeal
    val categories = viewModel.categories
    val searchResults = viewModel.searchResults
    val networkError = viewModel.networkError

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe Delight", color = Color.Blue) },
                actions = {
                    IconButton(onClick = { settingsViewModel.onToggleDarkMode() }) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDark) "Switch to light mode" else "Switch to dark mode",
                            tint = Color.Blue
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                RecipeSearchBar(
                    query = viewModel.searchQuery,
                    error = viewModel.searchError,
                    onQueryChange = { viewModel.onSearchQueryChange(it) },
                    onSearch = { viewModel.performSearch() },
                    onClear = { viewModel.clearSearch() },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            networkError?.let {
                item(span = { GridItemSpan(2) }) {
                    Text(text = it, color = Color.Red, modifier = Modifier.padding(8.dp))
                }
            }

            if (viewModel.isLoadingRandomMeal || viewModel.isLoadingCategories || viewModel.isSearching) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.Blue)
                    }
                }
            }

            else if (searchResults.isNotEmpty()) {
                items(searchResults) { meal ->
                    RandomMealCard(meal) { onMealClick(meal.id) }
                }
            }

            else {
                randomMeal?.let { meal ->
                    item(span = { GridItemSpan(2) }) {
                        Text(
                            text = "Featured Recipe",
                            fontSize = 20.sp,
                            color = Color.Blue,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    item(span = { GridItemSpan(2) }) {
                        RandomMealCard(meal) { onMealClick(meal.id) }
                    }
                }

                item(span = { GridItemSpan(2) }) {
                    Text(
                        text = "Categories",
                        fontSize = 20.sp,
                        color = Color.Blue,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                items(categories) { category ->
                    CategoryItem(category) { onCategoryClick(category.name) }
                }
            }
        }
    }
}

@Composable
fun RandomMealCard(meal: Meal, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable(
                onClickLabel = "View details for ${meal.name}",
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter(meal.thumb),
                contentDescription = "Photo of ${meal.name}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth()
            ) {
                Text(
                    text = meal.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(
                onClickLabel = "See recipes for ${category.name}",
                onClick = onClick
            ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box {
            category.thumb?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Category: ${category.name}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth()
            ) {
                Text(
                    text = category.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}