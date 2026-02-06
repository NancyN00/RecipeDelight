package com.nancy.recipedelight.ui.bookmark

import android.graphics.Color
import android.graphics.drawable.Icon
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nancy.recipedelight.domain.models.Meal
import com.nancy.recipedelight.navigation.Screen
import com.nancy.recipedelight.ui.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun BookmarkScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onMealClick: (String) -> Unit
) {
    // Collect the stream from our SQLDelight database
    val favorites by viewModel.bookmarkedMeals.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "My Favorites",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (favorites.isEmpty()) {
            // Simple Empty State
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No bookmarks yet. Start adding some!", color = MaterialTheme.colorScheme.primary)
            }
        } else {
            // Grid of Bookmarked Meals
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favorites) { meal ->
                    BookmarkMealCard(
                        meal = meal,
                        onMealClick = onMealClick,
                        onDeleteClick = { viewModel.toggleBookmark(meal) }
                    )
                }
            }
        }
    }
}

@Composable
fun BookmarkMealCard(
    meal: Meal,
    onMealClick: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
       // elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMealClick(meal.id) }
    ) {
        Column {
            Box {
                AsyncImage(
                    model = meal.thumb,
                    contentDescription = meal.name,
                    modifier = Modifier.height(120.dp).fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = androidx.compose.ui.graphics.Color.White
                    )

                }
            }

            Text(
                text = meal.name,
                modifier = Modifier.padding(8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}