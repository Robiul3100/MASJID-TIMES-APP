package com.robiul.mosquetime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.robiul.mosquetime.data.model.AppLanguage
import com.robiul.mosquetime.data.model.AppSettings
import com.robiul.mosquetime.data.model.CalculationMethod
import com.robiul.mosquetime.data.model.FontSizeScale

@Entity(tableName = "prayer_schedule_settings")
data class PrayerScheduleSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val selectedDistrictId: String = "dhaka",
    val calculationMethod: String = CalculationMethod.ISLAMIC_FOUNDATION_BD.name,
    val prayerReminderOffsetMinutes: Int = 15,
    val isAdhanSoundEnabled: Boolean = true,
    val isPrayerNotificationEnabled: Boolean = true,
    val isNoticeNotificationEnabled: Boolean = true,
    val isEventNotificationEnabled: Boolean = true,
    val isJumahReminderEnabled: Boolean = true,
    val fontSizeScale: String = FontSizeScale.MEDIUM.name,
    val language: String = AppLanguage.BENGALI.name,
    val isNeonGlowActive: Boolean = true,
    val isAutoLocationEnabled: Boolean = false,
    val customFajrOffset: Int = 0,
    val customDhuhrOffset: Int = 0,
    val customAsrOffset: Int = 0,
    val customMaghribOffset: Int = 0,
    val customIshaOffset: Int = 0,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): AppSettings {
        val calcMethod = try {
            CalculationMethod.valueOf(calculationMethod)
        } catch (e: Exception) {
            CalculationMethod.ISLAMIC_FOUNDATION_BD
        }

        val fontScale = try {
            FontSizeScale.valueOf(fontSizeScale)
        } catch (e: Exception) {
            FontSizeScale.MEDIUM
        }

        val appLang = try {
            AppLanguage.valueOf(language)
        } catch (e: Exception) {
            AppLanguage.BENGALI
        }

        return AppSettings(
            selectedDistrictId = selectedDistrictId,
            calculationMethod = calcMethod,
            prayerReminderOffsetMinutes = prayerReminderOffsetMinutes,
            isAdhanSoundEnabled = isAdhanSoundEnabled,
            isPrayerNotificationEnabled = isPrayerNotificationEnabled,
            isNoticeNotificationEnabled = isNoticeNotificationEnabled,
            isEventNotificationEnabled = isEventNotificationEnabled,
            isJumahReminderEnabled = isJumahReminderEnabled,
            fontSizeScale = fontScale,
            language = appLang,
            isNeonGlowActive = isNeonGlowActive,
            isAutoLocationEnabled = isAutoLocationEnabled
        )
    }

    companion object {
        fun fromDomainModel(settings: AppSettings): PrayerScheduleSettingsEntity {
            return PrayerScheduleSettingsEntity(
                id = 1,
                selectedDistrictId = settings.selectedDistrictId,
                calculationMethod = settings.calculationMethod.name,
                prayerReminderOffsetMinutes = settings.prayerReminderOffsetMinutes,
                isAdhanSoundEnabled = settings.isAdhanSoundEnabled,
                isPrayerNotificationEnabled = settings.isPrayerNotificationEnabled,
                isNoticeNotificationEnabled = settings.isNoticeNotificationEnabled,
                isEventNotificationEnabled = settings.isEventNotificationEnabled,
                isJumahReminderEnabled = settings.isJumahReminderEnabled,
                fontSizeScale = settings.fontSizeScale.name,
                language = settings.language.name,
                isNeonGlowActive = settings.isNeonGlowActive,
                isAutoLocationEnabled = settings.isAutoLocationEnabled,
                lastUpdatedTimestamp = System.currentTimeMillis()
            )
        }
    }
}
