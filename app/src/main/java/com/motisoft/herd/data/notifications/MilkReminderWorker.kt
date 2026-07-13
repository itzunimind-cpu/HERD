package com.motisoft.herd.data.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.motisoft.herd.MainActivity
import com.motisoft.herd.R
import com.motisoft.herd.data.local.dao.DailyMilkTotalDao
import com.motisoft.herd.data.local.dao.MilkDao
import com.motisoft.herd.data.repository.AuthRepository
import com.motisoft.herd.data.repository.BreedingRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

// Fires once daily per session (morning/evening), checks purely local Room data (no
// network), then reschedules itself for tomorrow via ReminderScheduler.
@HiltWorker
class MilkReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val milkDao: MilkDao,
    private val dailyMilkTotalDao: DailyMilkTotalDao,
    private val breedingRepository: BreedingRepository,
    private val authRepository: AuthRepository,
    private val reminderScheduler: ReminderScheduler,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val session = ReminderSession.entries.find { it.name == inputData.getString(SESSION_KEY) }
            ?: ReminderSession.MORNING
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

        if (!hasLoggedMilkToday(today)) {
            postMilkNotification(session)
        }

        if (session == ReminderSession.MORNING) {
            val due = breedingRepository.findCowsDueForHeatCheck(today)
            if (due.isNotEmpty()) postBreedingNotification(due)
        }

        reminderScheduler.reschedule(session)
        return Result.success()
    }

    private suspend fun hasLoggedMilkToday(today: LocalDate): Boolean {
        val ownerId = authRepository.currentUserId ?: return true
        val total = dailyMilkTotalDao.getByDate(ownerId, today)
        val hasTotal = total?.morningTotal != null || total?.eveningTotal != null
        return hasTotal || milkDao.existsForDate(today)
    }

    private fun postMilkNotification(session: ReminderSession) {
        if (!hasPostNotificationPermission()) return
        val text = if (session == ReminderSession.MORNING) {
            "सकाळचे दूध अजून नोंदवलेले नाही"
        } else {
            "संध्याकाळचे दूध अजून नोंदवलेले नाही"
        }
        val notification = NotificationCompat.Builder(applicationContext, MILK_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("आजचे दूध नोंदवा")
            .setContentText(text)
            .setAutoCancel(true)
            .setContentIntent(mainActivityPendingIntent())
            .build()
        NotificationManagerCompat.from(applicationContext).notify(MILK_NOTIFICATION_ID, notification)
    }

    private fun postBreedingNotification(due: List<Pair<String, String>>) {
        if (!hasPostNotificationPermission()) return
        val names = due.joinToString(", ") { it.second }
        val notification = NotificationCompat.Builder(applicationContext, BREEDING_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("माज तपासणी आवश्यक")
            .setContentText("$names - माजाची तपासणी करा")
            .setStyle(NotificationCompat.BigTextStyle().bigText("$names - माजाची तपासणी करा"))
            .setAutoCancel(true)
            .setContentIntent(mainActivityPendingIntent())
            .build()
        NotificationManagerCompat.from(applicationContext).notify(BREEDING_NOTIFICATION_ID, notification)
    }

    private fun mainActivityPendingIntent(): PendingIntent {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun hasPostNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val MILK_CHANNEL_ID = "herd_milk_reminders"
        const val BREEDING_CHANNEL_ID = "herd_breeding_reminders"
        private const val MILK_NOTIFICATION_ID = 1001
        private const val BREEDING_NOTIFICATION_ID = 1002
    }
}
