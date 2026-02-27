package com.nancy.recipedelight.workmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nancy.recipedelight.R
import com.nancy.recipedelight.domain.models.Meal
import com.nancy.recipedelight.domain.repositories.MealRepository

class MealWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val repository: MealRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("MealWorker", "Worker started successfully via Koin!")
        return try {
            val meal = repository.getRandomMeal()
            if (meal != null) {
                Log.d("MealWorker", "Meal fetched: ${meal.name}")
                showNotification(meal)
                Result.success()
            } else {
                Log.e("MealWorker", "Meal data was null from repository")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("MealWorker", "Error in Worker: ${e.message}")
            Result.retry()
        }
    }

    private fun showNotification(meal: Meal) {

        val channelId = "meal_channel_id"

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Meals",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily meal suggestions"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Today's Delight: ${meal.name}")
            .setContentText("Tap to see the recipe!")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
