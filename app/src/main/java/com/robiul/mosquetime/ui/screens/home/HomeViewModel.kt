package com.robiul.mosquetime.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.data.repository.UserPreferencesRepository
import com.robiul.mosquetime.ui.components.PrayerScheduleItem
import com.robiul.mosquetime.ui.components.PrayerType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val mosqueRepository: MosqueRepository
) : ViewModel() {

    private val TAG = "HomeViewModel"

    private val _liveTimeString = MutableStateFlow("12:00:00")
    val liveTimeString: StateFlow<String> = _liveTimeString.asStateFlow()

    private val _dateInfo = MutableStateFlow(DateInfo())
    val dateInfo: StateFlow<DateInfo> = _dateInfo.asStateFlow()

    private val _nextPrayerCountdown = MutableStateFlow("00:00:00")
    val nextPrayerCountdown: StateFlow<String> = _nextPrayerCountdown.asStateFlow()

    private val _nextPrayerName = MutableStateFlow("ফজর")
    val nextPrayerName: StateFlow<String> = _nextPrayerName.asStateFlow()

    private val _prayerSchedule = MutableStateFlow<List<PrayerScheduleItem>>(emptyList())
    val prayerSchedule: StateFlow<List<PrayerScheduleItem>> = _prayerSchedule.asStateFlow()

    init {
        // Observe district changes and start live clock
        viewModelScope.launch {
            UserPreferencesRepository.settings.collectLatest {
                updatePrayerTimesAndCountdown()
            }
        }
        startClock()
    }

    private fun startClock() {
        viewModelScope.launch {
            while (true) {
                val cal = Calendar.getInstance()
                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
                _liveTimeString.value = timeFormat.format(cal.time)

                val day = String.format(Locale.US, "%02d", cal.get(Calendar.DAY_OF_MONTH))
                val month = String.format(Locale.US, "%02d", cal.get(Calendar.MONTH) + 1)
                val year = String.format(Locale.US, "%02d", cal.get(Calendar.YEAR) % 100)

                val calDay = cal.get(Calendar.DAY_OF_WEEK)
                val dayIndex = when (calDay) {
                    Calendar.SATURDAY -> 0
                    Calendar.SUNDAY -> 1
                    Calendar.MONDAY -> 2
                    Calendar.TUESDAY -> 3
                    Calendar.WEDNESDAY -> 4
                    Calendar.THURSDAY -> 5
                    Calendar.FRIDAY -> 6
                    else -> 0
                }

                _dateInfo.value = DateInfo(day, month, year, dayIndex)

                // Update real prayer calculation and countdown
                updatePrayerTimesAndCountdown()

                delay(1000L)
            }
        }
    }

    private fun updatePrayerTimesAndCountdown() {
        val districtId = UserPreferencesRepository.settings.value.selectedDistrictId
        val todayPrayers = MosqueRepository.calculateTodayPrayers(districtId)
        val extraTimes = MosqueRepository.getExtraPrayerTimes(districtId)

        val cal = Calendar.getInstance()
        val currentSecondsToday = cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.SECOND)
        val isFriday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY

        // Helper to parse "04:52" or "12:15 PM" to seconds from midnight
        fun parseToSeconds(timeStr: String, isPmAssumption: Boolean = false): Int {
            return try {
                val clean = timeStr.replace("AM", "").replace("PM", "").trim()
                val parts = clean.split(":")
                var h = parts[0].trim().toInt()
                val m = parts[1].trim().take(2).toInt()
                if (isPmAssumption && h < 12) h += 12
                (h * 3600) + (m * 60)
            } catch (e: Exception) {
                0
            }
        }

        val fajrSec = parseToSeconds(todayPrayers.getOrNull(0)?.azanTime ?: "04:50", false)
        val dhuhrSec = parseToSeconds(todayPrayers.getOrNull(1)?.azanTime ?: "12:05", true)
        val asrSec = parseToSeconds(todayPrayers.getOrNull(2)?.azanTime ?: "04:35", true)
        val maghribSec = parseToSeconds(todayPrayers.getOrNull(3)?.azanTime ?: "06:18", true)
        val ishaSec = parseToSeconds(todayPrayers.getOrNull(4)?.azanTime ?: "07:35", true)

        // Determine active waqt and next waqt
        val isFajrActive = currentSecondsToday in fajrSec until (fajrSec + 90 * 60)
        val isDhuhrActive = currentSecondsToday in dhuhrSec until asrSec
        val isAsrActive = currentSecondsToday in asrSec until maghribSec
        val isMaghribActive = currentSecondsToday in maghribSec until ishaSec
        val isIshaActive = currentSecondsToday >= ishaSec || currentSecondsToday < fajrSec

        val prayerScheduleList = mutableListOf<PrayerScheduleItem>()

        // 1. Fajr
        val fajrItem = todayPrayers.getOrNull(0)
        prayerScheduleList.add(
            PrayerScheduleItem(
                type = PrayerType.FAJR,
                bengaliName = "ফজর",
                arabicName = "الفجر",
                time = fajrItem?.azanTime ?: "04:52",
                jamathTime = fajrItem?.iqamahTime ?: "05:15",
                isActive = isFajrActive,
                isPassed = currentSecondsToday >= (fajrSec + 90 * 60),
                isNext = currentSecondsToday < fajrSec,
                reminderNote = "ফজরের সুন্নত ও ফরজ অত্যন্ত ফযিলতপূর্ণ"
            )
        )

        // 2. Dhuhr / Jum'ah
        val dhuhrItem = todayPrayers.getOrNull(1)
        prayerScheduleList.add(
            PrayerScheduleItem(
                type = PrayerType.DHUHR,
                bengaliName = if (isFriday) "জুম'আ" else "যোহর",
                arabicName = if (isFriday) "الجمعة" else "الظهر",
                time = dhuhrItem?.azanTime ?: "12:05",
                jamathTime = dhuhrItem?.iqamahTime ?: "01:15",
                isJumah = isFriday,
                isActive = isDhuhrActive,
                isPassed = currentSecondsToday >= asrSec,
                isNext = currentSecondsToday in (fajrSec + 90 * 60) until dhuhrSec,
                reminderNote = if (isFriday) "জুম'আর খুতবা ও সালাতের জন্য মসজিদে দ্রুত উপস্থিত হোন" else "যোহরের ৪ রাকাত সুন্নত ও ৪ রাকাত ফরজ"
            )
        )

        // 3. Asr
        val asrItem = todayPrayers.getOrNull(2)
        prayerScheduleList.add(
            PrayerScheduleItem(
                type = PrayerType.ASR,
                bengaliName = "আসর",
                arabicName = "العصر",
                time = asrItem?.azanTime ?: "04:35",
                jamathTime = asrItem?.iqamahTime ?: "05:00",
                isActive = isAsrActive,
                isPassed = currentSecondsToday >= maghribSec,
                isNext = currentSecondsToday in dhuhrSec until asrSec,
                reminderNote = "সালাতুল আসর সংরক্ষণের বিশেষ তাগিদ রয়েছে"
            )
        )

        // 4. Maghrib
        val maghribItem = todayPrayers.getOrNull(3)
        prayerScheduleList.add(
            PrayerScheduleItem(
                type = PrayerType.MAGHRIB,
                bengaliName = "মাগরিব",
                arabicName = "المغرب",
                time = maghribItem?.azanTime ?: "06:18",
                jamathTime = maghribItem?.iqamahTime ?: "06:25",
                isActive = isMaghribActive,
                isPassed = currentSecondsToday >= ishaSec,
                isNext = currentSecondsToday in asrSec until maghribSec,
                reminderNote = "মাগরিবের ওয়াক্ত সীমিত, আজানের সাথে সাথে জামাতে শরিক হোন"
            )
        )

        // 5. Isha
        val ishaItem = todayPrayers.getOrNull(4)
        prayerScheduleList.add(
            PrayerScheduleItem(
                type = PrayerType.ISHA,
                bengaliName = "এশা",
                arabicName = "العشاء",
                time = ishaItem?.azanTime ?: "07:35",
                jamathTime = ishaItem?.iqamahTime ?: "08:15",
                isActive = isIshaActive,
                isPassed = currentSecondsToday < ishaSec && currentSecondsToday >= fajrSec,
                isNext = currentSecondsToday in maghribSec until ishaSec,
                reminderNote = "এশার ফরজ ও বিতরের নামাজ শেষে রাতের বিশ্রাম গ্রহণ করুন"
            )
        )

        // 6. Extra: Sunrise / Sehri
        val sehriItem = extraTimes.firstOrNull { it.name.contains("সেহরি") || it.name.contains("সূর্যোদয়") }
        prayerScheduleList.add(
            PrayerScheduleItem(
                type = PrayerType.SUNRISE_SEHRI,
                bengaliName = "সূর্যোদয় / সেহরি শেষ",
                arabicName = "الشروق",
                time = sehriItem?.time?.take(5) ?: "05:15",
                isExtra = true,
                reminderNote = "সূর্যোদয়ের ১৫ মিনিট পর ইশরাক নামাজের ওয়াক্ত শুরু হয়"
            )
        )

        // 7. Extra: Sunset / Iftar
        val iftarItem = extraTimes.firstOrNull { it.name.contains("ইফতার") || it.name.contains("সূর্যাস্ত") }
        prayerScheduleList.add(
            PrayerScheduleItem(
                type = PrayerType.SUNSET_IFTAR,
                bengaliName = "সূর্যাস্ত ও ইফতার",
                arabicName = "الغروب",
                time = iftarItem?.time?.take(5) ?: "06:18",
                isExtra = true,
                reminderNote = "সূর্যাস্তের সাথে সাথে ইফতার করা সুন্নাত"
            )
        )

        _prayerSchedule.value = prayerScheduleList

        // Calculate countdown to the next prayer
        val nextTargetSec: Int
        val nextName: String

        when {
            currentSecondsToday < fajrSec -> {
                nextTargetSec = fajrSec
                nextName = "ফজর"
            }
            currentSecondsToday < dhuhrSec -> {
                nextTargetSec = dhuhrSec
                nextName = if (isFriday) "জুম'আ" else "যোহর"
            }
            currentSecondsToday < asrSec -> {
                nextTargetSec = asrSec
                nextName = "আসর"
            }
            currentSecondsToday < maghribSec -> {
                nextTargetSec = maghribSec
                nextName = "মাগরিব"
            }
            currentSecondsToday < ishaSec -> {
                nextTargetSec = ishaSec
                nextName = "এশা"
            }
            else -> {
                // Next Fajr tomorrow
                nextTargetSec = fajrSec + 86400
                nextName = "ফজর (আগামীকাল)"
            }
        }

        _nextPrayerName.value = nextName

        val remainingSecs = (nextTargetSec - currentSecondsToday).coerceAtLeast(0)
        val hrs = remainingSecs / 3600
        val mins = (remainingSecs % 3600) / 60
        val secs = remainingSecs % 60
        _nextPrayerCountdown.value = String.format(Locale.US, "%02d:%02d:%02d", hrs, mins, secs)

        Log.d(TAG, "Loaded real prayer times for district: $districtId. Next: $nextName in ${_nextPrayerCountdown.value}")
    }

    data class DateInfo(
        val day: String = "01",
        val month: String = "09",
        val year: String = "26",
        val activeDayIndex: Int = 0
    )
}
