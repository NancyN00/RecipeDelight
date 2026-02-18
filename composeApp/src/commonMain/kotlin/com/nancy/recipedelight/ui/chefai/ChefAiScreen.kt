package com.nancy.recipedelight.ui.chefai

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nancy.recipedelight.ui.viewmodel.GeminiViewModel

@Composable
fun ChefAiScreen(viewModel: GeminiViewModel) {
    var userInput by remember { mutableStateOf("") }
    val chatHistory by viewModel.chatHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Welcome to Chef AI",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            items(chatHistory) { message ->
                ChatBubble(message)
            }

            // The Thinking Bubble
            if (isLoading) {
                item {
                    ThinkingBubble()
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                label = { Text("Ask about cooking...") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (userInput.isNotBlank()) {
                        viewModel.sendMessage(userInput)
                        userInput = ""
                    }
                },
                modifier = Modifier.wrapContentSize(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                enabled = !isLoading // Prevent double-sending
            ) {
                Text("Send")
            }
        }
    }
}

@Composable
fun ThinkingBubble() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            color = Color(0xFFF1F1F1),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 0.dp),
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Chef is thinking", style = MaterialTheme.typography.bodyMedium)
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

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Surface(
            color = containerColor,
            shape = RoundedCornerShape(
                topStart = 12.dp, topEnd = 12.dp,
                bottomStart = if (isUser) 12.dp else 0.dp,
                bottomEnd = if (isUser) 0.dp else 12.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                color = contentColor
            )
        }
    }
}
data class ChatMessage(
    val role: String, // "user" or "model"
    val text: String
)