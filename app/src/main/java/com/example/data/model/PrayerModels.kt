package com.example.data.model

import com.example.ui.components.PrayerType

data class PrayerTimeItem(
    val type: PrayerType,
    val bengaliName: String,
    val arabicName: String,
    val azanTime: String,
    val iqamahTime: String,
    val isJumah: Boolean = false,
    val isActive: Boolean = false,
    val isPassed: Boolean = false
)

data class ExtraPrayerTime(
    val name: String,
    val time: String,
    val description: String
)

data class MonthlyPrayerDay(
    val dayNumber: Int,
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
    val isToday: Boolean = false,
    val isFriday: Boolean = false
)

enum class CalculationMethod(val title: String, val description: String) {
    ISLAMIC_FOUNDATION_BD("ইসলামিক ফাউন্ডেশন বাংলাদেশ", "হানাফী মাযহাব (ডিফল্ট)"),
    UNIVERSITY_ISLAMIC_SCIENCES("করাচি বিশ্ববিদ্যালয়", "হানাফী মাযহাব"),
    UMM_AL_QURA("উম্ম আল-কুরা মক্কা", "শাফেঈ/হাম্বলী/মালিকী"),
    MWL("মুসলিম ওয়ার্ল্ড লীগ", "আন্তর্জাতিক মানদণ্ড")
}

data class District(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val fajrOffsetMinutes: Int = 0,
    val dhuhrOffsetMinutes: Int = 0,
    val asrOffsetMinutes: Int = 0,
    val maghribOffsetMinutes: Int = 0,
    val ishaOffsetMinutes: Int = 0
)
