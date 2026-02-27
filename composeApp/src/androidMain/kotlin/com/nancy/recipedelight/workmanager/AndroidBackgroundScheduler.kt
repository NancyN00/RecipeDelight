package com.nancy.recipedelight.workmanager

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.nancy.recipedelight.domain.repositories.BackgroundScheduler
import java.util.concurrent.TimeUnit

class AndroidBackgroundScheduler(private val context: Context) : BackgroundScheduler {
    override fun scheduleDailyMealNotification() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<MealWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_meal",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
