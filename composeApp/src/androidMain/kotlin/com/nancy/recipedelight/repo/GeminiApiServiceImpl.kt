package com.nancy.recipedelight.repo

import com.nancy.recipedelight.data.remote.GeminiApiService
import com.nancy.recipedelight.ui.chefai.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/** Makes the actual HTTP call to Gemini API.
 * Only exists in androidMain because it uses OkHttp, which is JVM/Android-only.
 *  Actual implementation for Android using OkHttp */

class GeminiApiServiceImpl(private val apiKey: String) : GeminiApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // List of messages for chat style
    override suspend fun generateContent(history: List<ChatMessage>): String {
        if (apiKey.isBlank() || apiKey == "null") {
            return "Error: API Key is missing."
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

        val json = JSONObject().apply {
            /* System Instruction: AI handles the "cooking only" rule */
            put("system_instruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", "You are a strict cooking assistant. If a user asks a question that is not about food, recipes, or cooking techniques, you must reply exactly with: 'I answer only cooking related questions.'")
                    })
                })
            })

            /* Build the conversation history */
            put("contents", JSONArray().apply {
                history.forEach { message ->
                    put(JSONObject().apply {
                        put("role", message.role) // "user" or "model"
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", message.text)
                            })
                        })
                    })
                }
            })
        }

        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder().url(url).post(body).build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string() ?: ""
                    if (!response.isSuccessful) return@withContext "Error ${response.code}"

                    val jsonResponse = JSONObject(responseBody)
                    jsonResponse.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")
                }
            } catch (e: Exception) {
                "Failed to connect: ${e.localizedMessage}"
            }
        }
    }
}