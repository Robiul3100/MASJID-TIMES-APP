package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.OfflinePrayerScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OfflinePrayerScheduleDao {

    @Query("SELECT * FROM offline_prayer_schedules WHERE year = :year AND month = :month AND districtId = :districtId ORDER BY dayNumber ASC")
    fun getMonthlyScheduleFlow(year: Int, month: Int, districtId: String): Flow<List<OfflinePrayerScheduleEntity>>

    @Query("SELECT * FROM offline_prayer_schedules WHERE year = :year AND month = :month AND districtId = :districtId ORDER BY dayNumber ASC")
    suspend fun getMonthlySchedule(year: Int, month: Int, districtId: String): List<OfflinePrayerScheduleEntity>

    @Query("SELECT * FROM offline_prayer_schedules WHERE year = :year AND month = :month AND dayNumber = :dayNumber AND districtId = :districtId LIMIT 1")
    suspend fun getDailySchedule(year: Int, month: Int, dayNumber: Int, districtId: String): OfflinePrayerScheduleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonthlySchedule(schedules: List<OfflinePrayerScheduleEntity>)

    @Query("DELETE FROM offline_prayer_schedules WHERE year = :year AND month = :month AND districtId = :districtId")
    suspend fun deleteMonthlySchedule(year: Int, month: Int, districtId: String)

    @Query("DELETE FROM offline_prayer_schedules")
    suspend fun clearAllCachedSchedules()
}
