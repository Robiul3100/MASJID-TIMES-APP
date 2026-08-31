package com.robiul.mosquetime.data.model

enum class AppLanguage(val code: String, val title: String) {
    BENGALI("bn", "বাংলা"),
    ENGLISH("en", "English")
}

enum class FontSizeScale(val title: String, val scaleFactor: Float) {
    SMALL("ছোট", 0.9f),
    MEDIUM("স্বাভাবিক (ডিফল্ট)", 1.0f),
    LARGE("বড় (সহজ পাঠ্য)", 1.15f),
    EXTRA_LARGE("অনেক বড় (বয়স্কদের জন্য)", 1.30f)
}

data class AppSettings(
    val selectedDistrictId: String = "dhaka",
    val calculationMethod: CalculationMethod = CalculationMethod.ISLAMIC_FOUNDATION_BD,
    val prayerReminderOffsetMinutes: Int = 15,
    val isAdhanSoundEnabled: Boolean = true,
    val isPrayerNotificationEnabled: Boolean = true,
    val isNoticeNotificationEnabled: Boolean = true,
    val isEventNotificationEnabled: Boolean = true,
    val isJumahReminderEnabled: Boolean = true,
    val fontSizeScale: FontSizeScale = FontSizeScale.MEDIUM,
    val language: AppLanguage = AppLanguage.BENGALI,
    val isNeonGlowActive: Boolean = true,
    val isAutoLocationEnabled: Boolean = false
)
