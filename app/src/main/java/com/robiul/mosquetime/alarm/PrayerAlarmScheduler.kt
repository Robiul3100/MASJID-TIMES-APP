package com.robiul.mosquetime.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.robiul.mosquetime.model.PrayerTimeItem
import com.robiul.mosquetime.receiver.PrayerAlarmReceiver
import java.util.Calendar

object PrayerAlarmScheduler {

    private const val TAG = "PrayerAlarmScheduler"
    private const val PREFS_NAME = "prayer_alarm_prefs"

    fun scheduleAllPrayers(context: Context, prayers: List<PrayerTimeItem>) {
        prayers.forEach { prayer ->
            if (prayer.isAlarmEnabled) {
                schedulePrayerAlarm(context, prayer)
            } else {
                cancelPrayerAlarm(context, prayer.id)
            }
        }
    }

    fun schedulePrayerAlarm(
        context: Context,
        prayer: PrayerTimeItem,
        offsetMinutes: Int = 0
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val (hour, minute) = parseTimeString(prayer.id, prayer.adhanTime)
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (offsetMinutes != 0) {
                add(Calendar.MINUTE, -offsetMinutes)
            }
            // If the time for today has already passed, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = "com.robiul.mosquetime.ACTION_PRAYER_ALARM"
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_ID, prayer.id)
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, prayer.nameBn)
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TIME, prayer.adhanTime)
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TITLE, "ওয়াক্ত শুরু: ${prayer.nameBn}")
        }

        val requestCode = getRequestCodeForPrayer(prayer.id)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            Log.d(TAG, "Scheduled alarm for ${prayer.nameBn} at ${calendar.time}")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException scheduling alarm: ${e.message}")
        }
    }

    fun cancelPrayerAlarm(context: Context, prayerId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = "com.robiul.mosquetime.ACTION_PRAYER_ALARM"
        }
        val requestCode = getRequestCodeForPrayer(prayerId)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    /**
     * Schedules a quick test alarm 5 seconds from now so the user can verify sound, vibration and notification.
     */
    fun scheduleTestAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val triggerTime = System.currentTimeMillis() + 5000L // 5 seconds

        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = "com.robiul.mosquetime.ACTION_PRAYER_ALARM"
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_ID, "test_alarm")
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, "টেস্ট এলার্ট")
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TIME, "টেস্ট")
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TITLE, "মসজিদ এলার্ট টেস্ট সফল!")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            9999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Test alarm schedule error: ${e.message}")
        }
    }

    fun rescheduleNextDay(context: Context, prayerId: String) {
        val prayers = listOf(
            PrayerTimeItem("fajr", "Fajr", "ফজর", "০৫:০২", "০৫:১৫"),
            PrayerTimeItem("dhuhr", "Dhuhr", "যোহর", "১২:১৫", "০১:১৫"),
            PrayerTimeItem("asr", "Asr", "আসর", "০৪:৪৫", "০৫:০০"),
            PrayerTimeItem("maghrib", "Maghrib", "মাগরিব", "০৬:২৪", "০৬:২৮"),
            PrayerTimeItem("isha", "Isha", "ইশা", "০৭:৪৫", "০৮:১৫")
        )
        val prayer = prayers.find { it.id == prayerId }
        if (prayer != null) {
            schedulePrayerAlarm(context, prayer)
        }
    }

    private fun getRequestCodeForPrayer(prayerId: String): Int {
        return when (prayerId) {
            "fajr" -> 101
            "dhuhr" -> 102
            "asr" -> 103
            "maghrib" -> 104
            "isha" -> 105
            else -> 100
        }
    }

    private fun parseTimeString(prayerId: String, timeStr: String): Pair<Int, Int> {
        val cleaned = timeStr
            .replace("০", "0")
            .replace("১", "1")
            .replace("২", "2")
            .replace("৩", "3")
            .replace("৪", "4")
            .replace("৫", "5")
            .replace("৬", "6")
            .replace("৭", "7")
            .replace("৮", "8")
            .replace("৯", "9")

        val parts = cleaned.split(":")
        var rawHour = parts.getOrNull(0)?.trim()?.toIntOrNull() ?: 12
        val rawMinute = parts.getOrNull(1)?.trim()?.take(2)?.toIntOrNull() ?: 0

        // Handle 12-hour clock adjustments for Dhuhr, Asr, Maghrib, Isha
        when (prayerId.lowercase()) {
            "fajr" -> {
                if (rawHour >= 12) rawHour = 0
            }
            "dhuhr" -> {
                if (rawHour < 11) rawHour += 12
            }
            "asr", "maghrib", "isha" -> {
                if (rawHour < 12) rawHour += 12
            }
        }

        return Pair(rawHour, rawMinute)
    }
}
