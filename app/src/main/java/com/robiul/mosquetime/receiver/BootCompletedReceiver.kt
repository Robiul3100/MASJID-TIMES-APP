package com.robiul.mosquetime.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.robiul.mosquetime.alarm.PrayerAlarmScheduler
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.service.MasjidFirebaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            Log.d("BootCompletedReceiver", "Device reboot detected ($action). Restoring prayer alarms and FCM.")

            // 1. Reschedule prayer alarms
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val defaultPrayers = listOf(
                        com.robiul.mosquetime.model.PrayerTimeItem("fajr", "Fajr", "ফজর", "05:00", "05:20"),
                        com.robiul.mosquetime.model.PrayerTimeItem("dhuhr", "Dhuhr", "যোহর", "01:15", "01:30"),
                        com.robiul.mosquetime.model.PrayerTimeItem("asr", "Asr", "আসর", "04:45", "05:00"),
                        com.robiul.mosquetime.model.PrayerTimeItem("maghrib", "Maghrib", "মাগরিব", "06:30", "06:35"),
                        com.robiul.mosquetime.model.PrayerTimeItem("isha", "Isha", "এশা", "08:00", "08:15")
                    )
                    PrayerAlarmScheduler.scheduleAllPrayers(context, defaultPrayers)
                    Log.d("BootCompletedReceiver", "Prayer alarms successfully restored on reboot")
                } catch (e: Exception) {
                    Log.e("BootCompletedReceiver", "Error restoring prayer alarms: ${e.message}")
                }
            }

            // 2. Re-verify FCM topic subscriptions
            try {
                MasjidFirebaseMessagingService.subscribeToDefaultTopics()
            } catch (e: Exception) {
                Log.w("BootCompletedReceiver", "Error resubscribing FCM topics on reboot: ${e.message}")
            }
        }
    }
}
