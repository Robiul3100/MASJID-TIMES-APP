package com.robiul.mosquetime

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.robiul.mosquetime.service.MasjidFirebaseMessagingService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MosqueApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 1. Initialize Firebase safely
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            Log.w("MosqueApplication", "FirebaseApp init deferred: ${e.message}")
        }

        // 2. Create System Notification Channels for Android 8.0+
        try {
            MasjidFirebaseMessagingService.createNotificationChannels(this)
        } catch (e: Exception) {
            Log.e("MosqueApplication", "Failed to create notification channels: ${e.message}")
        }

        // 3. Subscribe device to default mosque FCM topics
        try {
            MasjidFirebaseMessagingService.subscribeToDefaultTopics()
        } catch (e: Exception) {
            Log.w("MosqueApplication", "FCM topic init deferred: ${e.message}")
        }
    }
}

