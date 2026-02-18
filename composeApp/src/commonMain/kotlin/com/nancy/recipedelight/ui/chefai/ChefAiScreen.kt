package com.nancy.recipedelight.ui.chefai

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nancy.recipedelight.ui.viewmodel.GeminiViewModel

@Composable
fun ChefAiScreen(viewModel: GeminiViewModel) {
    var userInput by remember { mutableStateOf("") }
    val aiResponse by viewModel.result.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "Chef AI Icon",
            modifier = Modifier.size(64.dp),
        )

        Text(
            text = "Welcome to Chef AI",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Blue
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("Enter your recipe question") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (isRecipeQuestion(userInput)) {
                    viewModel.generate(userInput)
                } else {
                    viewModel.generate("Only cooking questions allowed. Example: How to bake a cake?")
                }
                userInput = ""
            },
            modifier = Modifier.wrapContentSize(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text("Ask Chef AI")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = aiResponse,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// Simple validation function
fun isRecipeQuestion(input: String): Boolean {
    val keywords = listOf(
        "recipe", "cook", "bake", "ingredients", "meal", "dish", "chef", "kitchen"
    )
    return keywords.any { input.lowercase().contains(it) }
}
