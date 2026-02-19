package com.nancy.recipedelight.ui.chefai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nancy.recipedelight.ui.viewmodel.GeminiViewModel
import org.koin.androidx.compose.koinViewModel

data class ChatMessage(
    val role: String, // "user" or "model"
    val text: String
)

@Composable
fun ChefAiScreen(viewModel: GeminiViewModel = koinViewModel()) {
    var userInput by remember { mutableStateOf("") }
    var showHistory by remember { mutableStateOf(false) } // Track if history is loaded

    val chatHistory by viewModel.chatHistory.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val mealId = "general"

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chef AI",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            )

            // History Button
            IconButton(onClick = {
                showHistory = true
                viewModel.loadChatHistory(mealId)
            }) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Load chat history",
                    tint = Color.Blue
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chat Area
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (chatHistory.isEmpty() && !isLoading && !showHistory) {
                // Placeholder before any messages or history
                Text(
                    text = "Ask me anything about cooking...",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chatHistory) { message ->
                        ChatBubble(message)
                    }

                    if (isLoading) {
                        item { ThinkingBubble() }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                label = { Text("Ask about cooking...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Blue,
                    focusedLabelColor = Color.Blue
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (userInput.isNotBlank()) {
                        viewModel.sendMessage(userInput, mealId = mealId)
                        userInput = ""
                    }
                },
                modifier = Modifier.wrapContentSize(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                enabled = !isLoading,
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send message",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Send")
            }
        }
    }
}

@Composable
fun ThinkingBubble() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            color = Color(0xFFF1F1F1),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 0.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Chef is thinking",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.semantics {
                        contentDescription = "Chef AI is generating a response"
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.Blue,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val containerColor = if (isUser) Color.Blue else Color(0xFFF1F1F1)
    val contentColor = if (isUser) Color.White else Color.Black

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = containerColor,
            shape = RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp,
                bottomStart = if (isUser) 12.dp else 0.dp,
                bottomEnd = if (isUser) 0.dp else 12.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier
                    .padding(12.dp)
                    .semantics {
                        contentDescription = if (isUser) "You said: ${message.text}" else "Chef AI said: ${message.text}"
                    },
                color = contentColor
            )
        }
    }
}
