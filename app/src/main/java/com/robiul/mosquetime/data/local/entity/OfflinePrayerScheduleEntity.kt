package com.robiul.mosquetime.data.local.entity

import androidx.room.Entity
import com.robiul.mosquetime.data.model.MonthlyPrayerDay

@Entity(
    tableName = "offline_prayer_schedules",
    primaryKeys = ["year", "month", "dayNumber", "districtId"]
)
data class OfflinePrayerScheduleEntity(
    val year: Int,
    val month: Int,
    val dayNumber: Int,
    val districtId: String,
    val bengaliDate: String,
    val hijriDate: String,
    val gregorianDate: String,
    val dayName: String,
    val fajrAzan: String,
    val fajrIqamah: String,
    val sunrise: String,
    val dhuhrAzan: String,
    val dhuhrIqamah: String,
    val asrAzan: String,
    val asrIqamah: String,
    val maghribAzan: String,
    val maghribIqamah: String,
    val ishaAzan: String,
    val ishaIqamah: String,
    val isFriday: Boolean = false,
    val cachedTimestamp: Long = System.currentTimeMillis()
) {
    fun toMonthlyPrayerDay(isToday: Boolean = false): MonthlyPrayerDay {
        return MonthlyPrayerDay(
            dayNumber = dayNumber,
            bengaliDate = bengaliDate,
            hijriDate = hijriDate,
            gregorianDate = gregorianDate,
            dayName = dayName,
            fajrAzan = fajrAzan,
            fajrIqamah = fajrIqamah,
            sunrise = sunrise,
            dhuhrAzan = dhuhrAzan,
            dhuhrIqamah = dhuhrIqamah,
            asrAzan = asrAzan,
            asrIqamah = asrIqamah,
            maghribAzan = maghribAzan,
            maghribIqamah = maghribIqamah,
            ishaAzan = ishaAzan,
            ishaIqamah = ishaIqamah,
            isToday = isToday,
            isFriday = isFriday
        )
    }

    companion object {
        fun fromMonthlyPrayerDay(
            day: MonthlyPrayerDay,
            year: Int,
            month: Int,
            districtId: String
        ): OfflinePrayerScheduleEntity {
            return OfflinePrayerScheduleEntity(
                year = year,
                month = month,
                dayNumber = day.dayNumber,
                districtId = districtId,
                bengaliDate = day.bengaliDate,
                hijriDate = day.hijriDate,
                gregorianDate = day.gregorianDate,
                dayName = day.dayName,
                fajrAzan = day.fajrAzan,
                fajrIqamah = day.fajrIqamah,
                sunrise = day.sunrise,
                dhuhrAzan = day.dhuhrAzan,
                dhuhrIqamah = day.dhuhrIqamah,
                asrAzan = day.asrAzan,
                asrIqamah = day.asrIqamah,
                maghribAzan = day.maghribAzan,
                maghribIqamah = day.maghribIqamah,
                ishaAzan = day.ishaAzan,
                ishaIqamah = day.ishaIqamah,
                isFriday = day.isFriday,
                cachedTimestamp = System.currentTimeMillis()
            )
        }
    }
}
