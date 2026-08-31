package com.robiul.mosquetime.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.robiul.mosquetime.MainActivity
import com.robiul.mosquetime.R
import com.robiul.mosquetime.data.firebase.FirestoreCollections
import com.robiul.mosquetime.data.model.AppNotification
import com.robiul.mosquetime.data.model.NotificationCategory
import com.robiul.mosquetime.data.repository.MosqueRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MasjidFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New Firebase Cloud Messaging Token generated: $token")
        
        // 1. Register Token to Firestore for targeted or broadcast admin triggers
        saveTokenToFirestore(token)

        // 2. Automatically subscribe device to default public mosque broadcast topics
        subscribeToDefaultTopics()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM Message received from: ${remoteMessage.from}")

        val data = remoteMessage.data
        val notification = remoteMessage.notification

        val title = notification?.title ?: data["title"] ?: getString(R.string.app_name)
        val message = notification?.body ?: data["message"] ?: data["body"] ?: ""
        val targetRoute = data["targetRoute"] ?: data["route"] ?: "notifications"
        val categoryStr = data["category"] ?: "GENERAL"
        val isEmergency = data["isEmergency"]?.toBoolean() ?: (categoryStr.equals("EMERGENCY", ignoreCase = true) || categoryStr.equals("JANAZA", ignoreCase = true))

        if (title.isNotBlank() || message.isNotBlank()) {
            // Display system push notification
            showPushNotification(
                title = title,
                body = message,
                targetRoute = targetRoute,
                isEmergency = isEmergency,
                categoryStr = categoryStr
            )

            // Save to in-memory/local repository so it immediately shows up in Notification tab
            saveToLocalHistory(title, message, targetRoute, categoryStr)
        }
    }

    private fun showPushNotification(
        title: String,
        body: String,
        targetRoute: String,
        isEmergency: Boolean,
        categoryStr: String
    ) {
        val channelId = if (isEmergency) {
            CHANNEL_EMERGENCY_ID
        } else if (categoryStr.equals("PRAYER", ignoreCase = true)) {
            CHANNEL_PRAYER_ID
        } else {
            CHANNEL_GENERAL_ID
        }

        // Create deep link intent to launch MainActivity and route to the destination screen
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("targetRoute", targetRoute)
            putExtra("notificationTitle", title)
            putExtra("notificationBody", body)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(if (isEmergency) NotificationCompat.PRIORITY_MAX else NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        if (isEmergency) {
            notificationBuilder.setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
            notificationBuilder.setLights(Color.RED, 1000, 1000)
        }

        val notificationId = (System.currentTimeMillis() % 100000).toInt()
        val manager = NotificationManagerCompat.from(this)

        try {
            manager.notify(notificationId, notificationBuilder.build())
        } catch (e: SecurityException) {
            Log.e(TAG, "Notification permission missing on Android 13+: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Error displaying notification: ${e.message}")
        }
    }

    private fun saveToLocalHistory(
        title: String,
        message: String,
        targetRoute: String,
        categoryStr: String
    ) {
        val category = try {
            NotificationCategory.valueOf(categoryStr.uppercase())
        } catch (e: Exception) {
            NotificationCategory.NOTICE
        }

        val nowTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val appNotification = AppNotification(
            id = "fcm_" + UUID.randomUUID().toString().take(8),
            title = title,
            message = message,
            timestamp = "আজ, $nowTime",
            timeAgo = "এইমাত্র",
            category = category,
            isRead = false,
            targetRoute = targetRoute
        )
        MosqueRepository.broadcastNotification(appNotification)
    }

    private fun saveTokenToFirestore(token: String) {
        serviceScope.launch {
            try {
                val firestore = FirebaseFirestore.getInstance()
                val tokenData = hashMapOf(
                    "token" to token,
                    "platform" to "Android",
                    "deviceModel" to "${Build.MANUFACTURER} ${Build.MODEL}",
                    "osVersion" to Build.VERSION.RELEASE,
                    "lastUpdated" to System.currentTimeMillis()
                )
                firestore.collection(FirestoreCollections.fcmTokensCollection())
                    .document(token)
                    .set(tokenData, SetOptions.merge())
                Log.d(TAG, "FCM Token registered successfully in Firestore")
            } catch (e: Exception) {
                Log.w(TAG, "Firestore token sync deferred (offline / unconfigured): ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "MasjidFCM"

        const val CHANNEL_GENERAL_ID = "channel_mosque_announcements"
        const val CHANNEL_EMERGENCY_ID = "channel_mosque_emergency"
        const val CHANNEL_PRAYER_ID = "channel_mosque_prayers"

        const val TOPIC_ANNOUNCEMENTS = "all_announcements"
        const val TOPIC_EMERGENCY = "emergency_alerts"
        const val TOPIC_PRAYERS = "prayer_alerts"
        const val TOPIC_RAMADAN = "ramadan_updates"

        fun subscribeToDefaultTopics() {
            try {
                val messaging = FirebaseMessaging.getInstance()
                messaging.subscribeToTopic(TOPIC_ANNOUNCEMENTS)
                messaging.subscribeToTopic(TOPIC_EMERGENCY)
                messaging.subscribeToTopic(TOPIC_PRAYERS)
                messaging.subscribeToTopic(TOPIC_RAMADAN)
                Log.d(TAG, "Subscribed device to default mosque FCM topics")
            } catch (e: Exception) {
                Log.w(TAG, "FCM topic subscription ignored offline: ${e.message}")
            }
        }

        fun sendLocalTestPushNotification(
            context: Context,
            title: String = "মসজিদ পুশ নোটিফিকেশন টেস্ট",
            body: String = "আলহামদুলিল্লাহ! আপনার ডিভাইসে রিয়েলটাইম পুশ নোটিফিকেশন সিস্টেম সম্পূর্ণ সক্রিয় আছে।",
            category: String = "NOTICE"
        ) {
            createNotificationChannels(context)
            val channelId = if (category.equals("EMERGENCY", true)) CHANNEL_EMERGENCY_ID else CHANNEL_GENERAL_ID

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("targetRoute", "notifications")
                putExtra("notificationTitle", title)
                putExtra("notificationBody", body)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

            val notificationId = (System.currentTimeMillis() % 100000).toInt()
            val manager = NotificationManagerCompat.from(context)

            try {
                manager.notify(notificationId, notificationBuilder.build())
            } catch (e: SecurityException) {
                Log.e(TAG, "Notification permission missing on Android 13+: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG, "Error displaying test notification: ${e.message}")
            }

            val nowTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val appNotification = AppNotification(
                id = "test_" + UUID.randomUUID().toString().take(8),
                title = title,
                message = body,
                timestamp = "আজ, $nowTime",
                timeAgo = "এইমাত্র",
                category = NotificationCategory.NOTICE,
                isRead = false,
                targetRoute = "notifications"
            )
            MosqueRepository.broadcastNotification(appNotification)
        }

        fun createNotificationChannels(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()

                // 1. General Announcements Channel
                val generalChannel = NotificationChannel(
                    CHANNEL_GENERAL_ID,
                    "মসজিদের নোটিশ ও ঘোষণা",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "মসজিদের সাধারণ নোটিশ, বয়ান, অনুষ্ঠান ও জামাত সংক্রান্ত নোটিফিকেশন"
                    enableLights(true)
                    lightColor = Color.GREEN
                    enableVibration(true)
                }

                // 2. Emergency & Janaza Channel (Max priority)
                val emergencyChannel = NotificationChannel(
                    CHANNEL_EMERGENCY_ID,
                    "জরুরি বার্তা ও জানাজা এলার্ট",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "জরুরি রক্তের প্রয়োজন, জানাজা এবং বিশেষ সতর্কতা বার্তা"
                    enableLights(true)
                    lightColor = Color.RED
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
                }

                // 3. Prayer Alerts Channel
                val prayerChannel = NotificationChannel(
                    CHANNEL_PRAYER_ID,
                    "নামাজের সময়সূচি ও আজান বার্তা",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "দৈনিক ৫ ওয়াক্ত নামাজের সময়, আজান ও জামাত কাউন্টডাউন বার্তা"
                    enableLights(true)
                    lightColor = Color.YELLOW
                    enableVibration(true)
                }

                notificationManager.createNotificationChannels(
                    listOf(generalChannel, emergencyChannel, prayerChannel)
                )
            }
        }
    }
}
