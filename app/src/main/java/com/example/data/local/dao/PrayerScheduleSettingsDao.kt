package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.PrayerScheduleSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerScheduleSettingsDao {

    @Query("SELECT * FROM prayer_schedule_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<PrayerScheduleSettingsEntity?>

    @Query("SELECT * FROM prayer_schedule_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): PrayerScheduleSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: PrayerScheduleSettingsEntity)

    @Query("UPDATE prayer_schedule_settings SET selectedDistrictId = :districtId, lastUpdatedTimestamp = :timestamp WHERE id = 1")
    suspend fun updateSelectedDistrict(districtId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE prayer_schedule_settings SET calculationMethod = :method, lastUpdatedTimestamp = :timestamp WHERE id = 1")
    suspend fun updateCalculationMethod(method: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE prayer_schedule_settings SET isPrayerNotificationEnabled = :enabled, lastUpdatedTimestamp = :timestamp WHERE id = 1")
    suspend fun updatePrayerNotification(enabled: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM prayer_schedule_settings")
    suspend fun clearSettings()
}
