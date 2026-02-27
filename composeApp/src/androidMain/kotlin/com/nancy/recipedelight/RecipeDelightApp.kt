package com.nancy.recipedelight

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.work.*
import com.nancy.recipedelight.di.androidModule
import com.nancy.recipedelight.di.appModule
import com.nancy.recipedelight.di.geminiModule
import com.nancy.recipedelight.di.viewModelModule
import com.nancy.recipedelight.workmanager.MealWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class RecipeDelightApp : Application(), Configuration.Provider {

    // Provide the WorkManager Configuration using Koin's Factory
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(KoinWorkerFactory())
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

    override fun onCreate() {
        super.onCreate()

        // Start Koin FIRST so the WorkerFactory is ready
        startKoin {
            androidContext(this@RecipeDelightApp)
            modules(appModule, viewModelModule, androidModule, geminiModule)
        }

        // Setup Notifications
        createNotificationChannel()

        // Initialize and Schedule Work
        val workManager = WorkManager.getInstance(this)
        setupWork(workManager)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Meal Recommendations"
            val descriptionText = "Daily meal and recipe suggestions"
            val importance = NotificationManager.IMPORTANCE_HIGH

            // This ID "meal_channel_id" MUST match the MealWorker.kt
            val channel = NotificationChannel("meal_channel_id", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setupWork(workManager: WorkManager) {
        // Immediate Request (For testing and first run)
        val immediateRequest = OneTimeWorkRequestBuilder<MealWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        workManager.enqueueUniqueWork(
            "immediate_meal_notif",
            ExistingWorkPolicy.REPLACE,
            immediateRequest
        )

        // Periodic Request (24-hour cycle)
        val dailyRequest = PeriodicWorkRequestBuilder<MealWorker>(24, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            "daily_meal_recipe",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyRequest
        )
    }
}
