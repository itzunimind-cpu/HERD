package com.motisoft.herd.data.notifications

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class ReminderTime(val hour: Int, val minute: Int)

enum class ReminderSession(val prefKeyPrefix: String, val defaultHour: Int) {
    MORNING("morning", 7),
    EVENING("evening", 18),
}

// Reminder times are a per-device UI preference, not farm data - kept local only,
// not synced through Room/Supabase like the rest of the app's data.
@Singleton
class NotificationSettingsRepository @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("herd_notification_prefs", Context.MODE_PRIVATE)

    fun getTime(session: ReminderSession): ReminderTime {
        val hour = prefs.getInt("${session.prefKeyPrefix}_hour", session.defaultHour)
        val minute = prefs.getInt("${session.prefKeyPrefix}_minute", 0)
        return ReminderTime(hour, minute)
    }

    fun setTime(session: ReminderSession, hour: Int, minute: Int) {
        prefs.edit {
            putInt("${session.prefKeyPrefix}_hour", hour)
            putInt("${session.prefKeyPrefix}_minute", minute)
        }
    }
}
