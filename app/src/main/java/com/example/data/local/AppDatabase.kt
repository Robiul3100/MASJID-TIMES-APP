package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.local.dao.*
import com.example.data.local.entity.*

@Database(
    entities = [
        PrayerScheduleSettingsEntity::class,
        UserLocationEntity::class,
        OfflinePrayerScheduleEntity::class,
        TasbihRecordEntity::class,
        UserQuestionEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prayerScheduleSettingsDao(): PrayerScheduleSettingsDao
    abstract fun userLocationDao(): UserLocationDao
    abstract fun offlinePrayerScheduleDao(): OfflinePrayerScheduleDao
    abstract fun tasbihDao(): TasbihDao
    abstract fun userQuestionDao(): UserQuestionDao
}
