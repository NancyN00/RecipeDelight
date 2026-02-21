package com.nancy.recipedelight.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
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
    val randomMeal = viewModel.randomMeal
    val categories = viewModel.categories
    val isDark by settingsViewModel.isDarkMode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Recipe Delight",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Blue
                    )
                },
                actions = {
                    IconButton(
                        onClick = { settingsViewModel.onToggleDarkMode() },
                        modifier = Modifier.semantics {
                            contentDescription = if (isDark) "Switch to Light Mode" else "Switch to Dark Mode"
                        }
                    ) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = Color.Blue
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
        ) {

            RecipeSearchBar(
                query = viewModel.searchQuery,
                error = viewModel.searchError,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                onSearch = { viewModel.performSearch() },
                onClear = { viewModel.clearSearch() },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            // Random Meal Card
            randomMeal?.let { meal: Meal ->
                RandomMealCard(meal) {
                    onMealClick(meal.id)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Categories Grid
            Text(
                text = "Categories",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.semantics { contentDescription = "Recipe Categories Section" }
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxHeight()
            ) {
                items(items = categories) { category: Category ->
                    CategoryItem(category) {
                        onCategoryClick(category.name)
                    }
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
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter(meal.thumb),
                contentDescription = "Featured meal: ${meal.name}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
            ) {
                Text(
                    text = meal.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
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
                onClickLabel = "View category ${category.name}",
                onClick = onClick
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box {
            category.thumb?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Category image for ${category.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
            ) {
                Text(
                    text = category.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}