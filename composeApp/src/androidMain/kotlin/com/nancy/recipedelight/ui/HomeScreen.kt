package com.nancy.recipedelight.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.nancy.recipedelight.domain.models.Category
import com.nancy.recipedelight.domain.models.Meal
import com.nancy.recipedelight.ui.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val randomMeal by viewModel.randomMeal.collectAsState(initial = null)
    val categories by viewModel.categories.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Random Meal Card
        randomMeal?.let { meal ->
            RandomMealCard(meal) {
                // TODO: navigate to Meal Details
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Categories Grid
        Text(
            text = "Categories",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            items(categories) { category ->
                CategoryItem(category) {
                    // TODO: fetch meals by category
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
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter(meal.thumb),
                contentDescription = meal.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = meal.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
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
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box {
            category.thumb?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = category.name,
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
                    modifier = Modifier.padding(6.dp)
                )
            }
        }
    }
}
