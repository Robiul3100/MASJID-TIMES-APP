package com.robiul.mosquetime.feature.admin.prayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robiul.mosquetime.core.auth.AdminUser
import com.robiul.mosquetime.data.firebase.CustomPrayerOverride
import com.robiul.mosquetime.data.firebase.MosqueAdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

sealed class PrayerScheduleUiEvent {
    data class ShowMessage(val message: String, val isError: Boolean = false) : PrayerScheduleUiEvent()
}

@HiltViewModel
class AdminPrayerScheduleViewModel @Inject constructor(
    private val repository: MosqueAdminRepository
) : ViewModel() {

    val currentOverrides: StateFlow<CustomPrayerOverride> = repository.prayerOverrides
    val isLoading: StateFlow<Boolean> = repository.isLoading

    private val _eventFlow = MutableSharedFlow<PrayerScheduleUiEvent>()
    val eventFlow: SharedFlow<PrayerScheduleUiEvent> = _eventFlow.asSharedFlow()

    // 5 Waqt Timings (Azan & Iqamah)
    val fajrAzan = MutableStateFlow("04:35")
    val fajrIqamah = MutableStateFlow("05:05")

    val dhuhrAzan = MutableStateFlow("12:00")
    val dhuhrIqamah = MutableStateFlow("01:15")

    val asrAzan = MutableStateFlow("04:30")
    val asrIqamah = MutableStateFlow("05:00")

    val maghribAzan = MutableStateFlow("06:35")
    val maghribIqamah = MutableStateFlow("06:45")

    val ishaAzan = MutableStateFlow("07:55")
    val ishaIqamah = MutableStateFlow("08:30")

    // Jumah Timings
    val jumahAzan1 = MutableStateFlow("12:45")
    val jumahKhutbah = MutableStateFlow("01:15")
    val jumahJamath = MutableStateFlow("01:30")

    // Extra / Fasting / Nafil
    val sehriEnd = MutableStateFlow("03:58")
    val iftarTime = MutableStateFlow("06:36")
    val tahajjudTime = MutableStateFlow("01:30 - 03:45")
    val ishraqTime = MutableStateFlow("05:22")
    val chashtTime = MutableStateFlow("08:15 - 11:15")

    val isCustomActive = MutableStateFlow(true)
    val broadcastNotification = MutableStateFlow(true)

    init {
        populateForm(currentOverrides.value)
    }

    fun populateForm(override: CustomPrayerOverride) {
        fajrAzan.value = override.fajrAzan
        fajrIqamah.value = override.fajrIqamah
        dhuhrAzan.value = override.dhuhrAzan
        dhuhrIqamah.value = override.dhuhrIqamah
        asrAzan.value = override.asrAzan
        asrIqamah.value = override.asrIqamah
        maghribAzan.value = override.maghribAzan
        maghribIqamah.value = override.maghribIqamah
        ishaAzan.value = override.ishaAzan
        ishaIqamah.value = override.ishaIqamah
        jumahAzan1.value = override.jumahAzan1
        jumahKhutbah.value = override.jumahKhutbah
        jumahJamath.value = override.jumahJamath
        sehriEnd.value = override.sehriEnd
        iftarTime.value = override.iftarTime
        tahajjudTime.value = override.tahajjudTime
        ishraqTime.value = override.ishraqTime
        chashtTime.value = override.chashtTime
        isCustomActive.value = override.isCustomScheduleActive
    }

    fun adjustTime(timeFlow: MutableStateFlow<String>, offsetMinutes: Int) {
        try {
            val parts = timeFlow.value.split(":")
            var h = parts[0].trim().toInt()
            val m = parts[1].trim().take(2).toInt()

            var totalMins = h * 60 + m + offsetMinutes
            if (totalMins < 0) totalMins += 1440
            totalMins %= 1440

            var newH = totalMins / 60
            val newM = totalMins % 60
            if (newH > 12) newH -= 12
            if (newH == 0) newH = 12

            timeFlow.value = String.format(Locale.US, "%02d:%02d", newH, newM)
        } catch (e: Exception) {
            // Ignore parse failures
        }
    }

    fun saveSchedule(adminUser: AdminUser?) {
        viewModelScope.launch {
            val updated = CustomPrayerOverride(
                fajrAzan = fajrAzan.value.trim(),
                fajrIqamah = fajrIqamah.value.trim(),
                dhuhrAzan = dhuhrAzan.value.trim(),
                dhuhrIqamah = dhuhrIqamah.value.trim(),
                asrAzan = asrAzan.value.trim(),
                asrIqamah = asrIqamah.value.trim(),
                maghribAzan = maghribAzan.value.trim(),
                maghribIqamah = maghribIqamah.value.trim(),
                ishaAzan = ishaAzan.value.trim(),
                ishaIqamah = ishaIqamah.value.trim(),
                jumahAzan1 = jumahAzan1.value.trim(),
                jumahKhutbah = jumahKhutbah.value.trim(),
                jumahJamath = jumahJamath.value.trim(),
                sehriEnd = sehriEnd.value.trim(),
                iftarTime = iftarTime.value.trim(),
                tahajjudTime = tahajjudTime.value.trim(),
                ishraqTime = ishraqTime.value.trim(),
                chashtTime = chashtTime.value.trim(),
                isCustomScheduleActive = isCustomActive.value,
                lastUpdated = System.currentTimeMillis(),
                updatedBy = adminUser?.nameBn ?: "Admin"
            )

            repository.savePrayerSchedule(updated, adminUser)

            val msg = if (broadcastNotification.value) {
                "নামাজের নতুন সময়সূচি সংরক্ষিত ও লাইভ আপডেট সম্পন্ন হয়েছে!"
            } else {
                "সময়সূচি সংরক্ষিত হয়েছে!"
            }
            _eventFlow.emit(PrayerScheduleUiEvent.ShowMessage(msg))
        }
    }
}
