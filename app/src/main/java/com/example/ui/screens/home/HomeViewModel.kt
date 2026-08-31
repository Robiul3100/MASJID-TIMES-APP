package com.example.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MosqueRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.ui.components.PrayerScheduleItem
import com.example.ui.components.PrayerType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val mosqueRepository: MosqueRepository
) : ViewModel() {

    private val _liveTimeString = MutableStateFlow("18:88:88")
    val liveTimeString: StateFlow<String> = _liveTimeString.asStateFlow()

    private val _dateInfo = MutableStateFlow(DateInfo())
    val dateInfo: StateFlow<DateInfo> = _dateInfo.asStateFlow()

    private val _nextPrayerCountdown = MutableStateFlow("02:34:56")
    val nextPrayerCountdown: StateFlow<String> = _nextPrayerCountdown.asStateFlow()

    private val _prayerSchedule = MutableStateFlow<List<PrayerScheduleItem>>(emptyList())
    val prayerSchedule: StateFlow<List<PrayerScheduleItem>> = _prayerSchedule.asStateFlow()

    init {
        startClock()
        loadPrayerSchedule()
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

                // Simple countdown logic (to be replaced with real calculation)
                val totalSecs = (3600 * 2 + 34 * 60 + 56 - (cal.get(Calendar.SECOND))) % 86400
                val hrs = (totalSecs / 3600).coerceAtLeast(0)
                val mins = ((totalSecs % 3600) / 60).coerceAtLeast(0)
                val secs = (totalSecs % 60).coerceAtLeast(0)
                _nextPrayerCountdown.value = String.format(Locale.US, "%02d:%02d:%02d", hrs, mins, secs)

                delay(1000L)
            }
        }
    }

    private fun loadPrayerSchedule() {
        // Default Prayer Schedule Items matching visual design
        _prayerSchedule.value = listOf(
            PrayerScheduleItem(PrayerType.FAJR, "ফজর", "الفجر", "8:88"),
            PrayerScheduleItem(PrayerType.DHUHR, "যোহর", "الظهر", "8:88"),
            PrayerScheduleItem(PrayerType.ASR, "আসর", "العصر", "8:88"),
            PrayerScheduleItem(PrayerType.MAGHRIB, "মাগরিব", "المغرب", "8:88"),
            PrayerScheduleItem(PrayerType.ISHA, "এশা", "العشاء", "8:88"),
            PrayerScheduleItem(PrayerType.JUMAH, "জুম'আ", "الجمعة", "8:88", isJumah = true, isActive = true),
            PrayerScheduleItem(PrayerType.SUNRISE_SEHRI, "সূর্যোদয়/সেহরি", "الشروق", "8:88"),
            PrayerScheduleItem(PrayerType.SUNSET_IFTAR, "সূর্যাস্ত/ইফতার", "الغرুর", "8:88")
        )
    }

    data class DateInfo(
        val day: String = "88",
        val month: String = "88",
        val year: String = "88",
        val activeDayIndex: Int = 0
    )
}
