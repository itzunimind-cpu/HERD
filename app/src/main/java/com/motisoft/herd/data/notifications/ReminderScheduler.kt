package com.motisoft.herd.data.notifications

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.todayIn
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

const val SESSION_KEY = "session"

// Reminders are one-shot WorkRequests that reschedule themselves a day later when
// they run (see MilkReminderWorker), rather than AlarmManager exact alarms - this
// needs no SCHEDULE_EXACT_ALARM permission and WorkManager survives reboots on its
// own, at the cost of exact-to-the-minute timing (fine for a "log your milk" nudge).
@Singleton
class ReminderScheduler @Inject constructor(
    private val workManager: WorkManager,
    private val settingsRepository: NotificationSettingsRepository,
) {
    fun ensureScheduled() {
        ReminderSession.entries.forEach { session ->
            workManager.enqueueUniqueWork(
                uniqueWorkName(session),
                ExistingWorkPolicy.KEEP,
                buildRequest(session),
            )
        }
    }

    fun reschedule(session: ReminderSession) {
        workManager.enqueueUniqueWork(
            uniqueWorkName(session),
            ExistingWorkPolicy.REPLACE,
            buildRequest(session),
        )
    }

    private fun buildRequest(session: ReminderSession) =
        OneTimeWorkRequestBuilder<MilkReminderWorker>()
            .setInitialDelay(delayMillisUntilNext(session), TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(SESSION_KEY to session.name))
            .build()

    private fun delayMillisUntilNext(session: ReminderSession): Long {
        val time = settingsRepository.getTime(session)
        val timeZone = TimeZone.currentSystemDefault()
        val now = Clock.System.now()
        val today = Clock.System.todayIn(timeZone)
        var target = today.atTime(time.hour, time.minute).toInstant(timeZone)
        if (target <= now) {
            target = today.plus(1, DateTimeUnit.DAY).atTime(time.hour, time.minute).toInstant(timeZone)
        }
        return (target - now).inWholeMilliseconds
    }

    private fun uniqueWorkName(session: ReminderSession) = "reminder_${session.prefKeyPrefix}"
}
