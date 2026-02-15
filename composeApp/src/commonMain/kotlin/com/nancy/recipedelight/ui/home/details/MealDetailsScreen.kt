package com.nancy.recipedelight.ui.home.details

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.nancy.recipedelight.domain.models.Meal
import com.nancy.recipedelight.domain.repositories.MealRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailsScreen(
    mealId: String,
    repository: MealRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var meal by remember { mutableStateOf<Meal?>(null) }

    // AI Sheet State
    var showAiSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

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
                    // AI Sparkle Icon
                    IconButton(onClick = { showAiSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Ask Chef AI about this recipe",
                            tint = Color.Blue
                        )
                    }

                    meal?.let { currentMeal ->
                        IconButton(onClick = {
                            scope.launch {
                                val wasBookmarked = isBookmarked
                                repository.toggleBookmark(currentMeal)
                                if (!wasBookmarked) {
                                    Toast.makeText(context, "meal added on details screen", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = if (isBookmarked) "Remove bookmark" else "Add bookmark",
                                tint = if (isBookmarked) Color.Blue else Color.Gray
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
                        .height(250.dp),
                    contentScale = ContentScale.Crop
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
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                m.ingredients.forEach { ingredient ->
                    Text(
                        text = "• $ingredient",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 24.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Instructions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                Text(
                    text = m.instructions ?: "-",
                    style = MaterialTheme.typography.bodyMedium,
                 //   modifier = Modifier.padding(horizontal = 16.dp, bottom = 32.dp)
                )
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Blue)
            }
        }

        // AI Chat Bottom Sheet
        if (showAiSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAiSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                // pass the meal name and ingredients as context for Gemini
                ChefAiChatContent(
                    mealContext = meal?.let { "${it.name}. Ingredients: ${it.ingredients.joinToString()}" }
                )
            }
        }
    }
}

@Composable
fun ChefAiChatContent(mealContext: String?) {
    var userInput by remember { mutableStateOf("") }
    // A simple list to store the conversation: Pair(Message, IsUser)
    val chatMessages = remember { mutableStateListOf<Pair<String, Boolean>>() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .padding(16.dp)
    ) {
        Text(
            text = "Chef AI Helper",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Blue,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Ask me anything about this recipe!",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Divider()

        // Placeholder for Chat List
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            chatMessages.forEach { message ->
                ChatBubble(text = message.first, isUser = message.second)
            }
        }

        // Input Area
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        ) {
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("How do I cook this?") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF0F0F0),
                    unfocusedContainerColor = Color(0xFFF0F0F0)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            IconButton(onClick = {
                if (userInput.isNotBlank()) {
                    chatMessages.add(userInput to true)
                    userInput = ""
                    // AI Response logic here
                }
            }) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Blue)
            }
        }
    }
}

@Composable
fun ChatBubble(text: String, isUser: Boolean) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (isUser) Color.Blue else Color.LightGray,
            contentColor = if (isUser) Color.White else Color.Black,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = text, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.bodyMedium)
        }
    }
}