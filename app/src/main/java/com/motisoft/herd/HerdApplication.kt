package com.motisoft.herd

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.motisoft.herd.data.notifications.MilkReminderWorker
import com.motisoft.herd.data.notifications.ReminderScheduler
import com.motisoft.herd.data.sync.SyncScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HerdApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var syncScheduler: SyncScheduler
    @Inject lateinit var reminderScheduler: ReminderScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        syncScheduler.schedulePeriodic()
        createNotificationChannels()
        reminderScheduler.ensureScheduled()
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                MilkReminderWorker.MILK_CHANNEL_ID,
                "दूध आठवण",
                NotificationManager.IMPORTANCE_DEFAULT,
            ),
        )
        manager.createNotificationChannel(
            NotificationChannel(
                MilkReminderWorker.BREEDING_CHANNEL_ID,
                "माज तपासणी आठवण",
                NotificationManager.IMPORTANCE_DEFAULT,
            ),
        )
    }
}
