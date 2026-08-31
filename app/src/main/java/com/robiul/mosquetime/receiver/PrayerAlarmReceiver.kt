package com.robiul.mosquetime.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.robiul.mosquetime.MainActivity
import com.robiul.mosquetime.R
import com.robiul.mosquetime.alarm.PrayerAlarmScheduler
import com.robiul.mosquetime.util.HapticUtils

class PrayerAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "prayer_alerts_channel_v1"
        const val EXTRA_PRAYER_ID = "extra_prayer_id"
        const val EXTRA_PRAYER_NAME = "extra_prayer_name"
        const val EXTRA_PRAYER_TIME = "extra_prayer_time"
        const val EXTRA_PRAYER_TITLE = "extra_prayer_title"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prayerId = intent.getStringExtra(EXTRA_PRAYER_ID) ?: "prayer"
        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: "নামাজ"
        val prayerTime = intent.getStringExtra(EXTRA_PRAYER_TIME) ?: ""
        val prayerTitle = intent.getStringExtra(EXTRA_PRAYER_TITLE) ?: "নামাজের সময় হয়েছে"

        // Haptic feedback pulse on device
        HapticUtils.performTactilePulse(context, 100)

        // Show Islamic Notification with sound & vibration
        showPrayerNotification(context, prayerId, prayerName, prayerTime, prayerTitle)

        // Reschedule next recurring cycle
        PrayerAlarmScheduler.rescheduleNextDay(context, prayerId)
    }

    private fun showPrayerNotification(
        context: Context,
        prayerId: String,
        prayerName: String,
        prayerTime: String,
        prayerTitle: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Create High Importance Notification Channel on Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                "নামাজের সময়সূচি ও আজান এলার্ট",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "চৌধুরী পাটোয়ারী বাড়ি জামে মসজিদ - ওয়াক্তভিত্তিক নামাজের এলার্ট ও আজান স্মরণবার্তা"
                enableLights(true)
                lightColor = 0xFF00E676.toInt()
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300, 200, 500)
                setSound(soundUri, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Tap Intent to open Main Activity
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "schedule")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            prayerId.hashCode(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("🕌 $prayerTitle ($prayerName)")
            .setContentText("চৌধুরী পাটোয়ারী বাড়ি জামে মসজিদ — $prayerName এর ওয়াক্ত শুরু হয়েছে ($prayerTime)")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "আস-সালাতু খাইরুম মিনান নাওম / নামাজের সময় হয়েছে।\n" +
                                "ওয়াক্ত: $prayerName ($prayerTime)\n" +
                                "মসজিদ: চৌধুরী পাটোয়ারী বাড়ি জামে মসজিদ"
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 300, 200, 300, 200, 500))
            .build()

        val notificationId = prayerId.hashCode()
        notificationManager.notify(notificationId, notification)
    }
}
