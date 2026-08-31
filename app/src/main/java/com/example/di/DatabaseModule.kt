package com.example.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mosque_offline_prayer.db"
        )
        .fallbackToDestructiveMigration() // Version changed from 3 manually to Room
        .build()
    }

    @Provides
    fun providePrayerScheduleSettingsDao(db: AppDatabase): PrayerScheduleSettingsDao = db.prayerScheduleSettingsDao()

    @Provides
    fun provideUserLocationDao(db: AppDatabase): UserLocationDao = db.userLocationDao()

    @Provides
    fun provideOfflinePrayerScheduleDao(db: AppDatabase): OfflinePrayerScheduleDao = db.offlinePrayerScheduleDao()

    @Provides
    fun provideTasbihDao(db: AppDatabase): TasbihDao = db.tasbihDao()

    @Provides
    fun provideUserQuestionDao(db: AppDatabase): UserQuestionDao = db.userQuestionDao()
}
