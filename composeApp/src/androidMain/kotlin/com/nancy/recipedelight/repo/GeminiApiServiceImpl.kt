package com.nancy.recipedelight.repo

import com.nancy.recipedelight.BuildConfig
import java.util.concurrent.TimeUnit
import com.nancy.recipedelight.data.remote.GeminiApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

/** Makes the actual HTTP call to Gemini API.
 * Only exists in androidMain because it uses OkHttp, which is JVM/Android-only.
 *  Actual implementation for Android using OkHttp */

class GeminiApiServiceImpl(private val apiKey: String) : GeminiApiService {

    /* Increase timeouts to handle long AI generations */
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Time to establish connection
        .readTimeout(60, TimeUnit.SECONDS)    // Time to wait for the recipe text
        .writeTimeout(30, TimeUnit.SECONDS)   // Time to send the prompt
        .build()

    override suspend fun generateContent(prompt: String): String {
        if (apiKey.isBlank() || apiKey == "null") {
            return "Error: API Key is missing from local.properties"
        }

     //   val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

        // Correct JSON for Gemini API
        val json = JSONObject().apply {
            /* System Instruction */
            put("system_instruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", "You are a strict cooking assistant. If a user asks a question that is not about food, recipes, or cooking techniques, you must reply exactly with: 'I answer only cooking related questions.'")
                    })
                })
            })

            /* Add User Content */
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }

        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder().url(url).post(body).build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string() ?: ""
                    if (!response.isSuccessful) {
                        return@withContext "Error ${response.code}: $responseBody"
                    }

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