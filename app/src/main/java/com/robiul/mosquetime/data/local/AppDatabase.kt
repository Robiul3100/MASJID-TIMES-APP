package com.robiul.mosquetime.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.robiul.mosquetime.data.local.dao.*
import com.robiul.mosquetime.data.local.entity.*

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

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mosque_offline_prayer.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { instance = it }
            }
        }
    }
}

