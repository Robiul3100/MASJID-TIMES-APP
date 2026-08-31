package com.example.data.repository

import android.content.Context
import com.example.data.model.AppSettings
import com.example.data.model.DonationRecord
import com.example.data.model.QuranBookmark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object UserPreferencesRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var offlineRepo: OfflinePrayerRepository? = null

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private val _bookmarkedDuaIds = MutableStateFlow<Set<String>>(setOf("dua_1", "dua_3"))
    val bookmarkedDuaIds: StateFlow<Set<String>> = _bookmarkedDuaIds.asStateFlow()

    private val _readNotificationIds = MutableStateFlow<Set<String>>(setOf("notif_3", "notif_4"))
    val readNotificationIds: StateFlow<Set<String>> = _readNotificationIds.asStateFlow()

    private val _tasbeehCounts = MutableStateFlow<Map<String, Int>>(
        mapOf("t1" to 12, "t2" to 33, "t3" to 5, "t4" to 47, "t5" to 20, "t6" to 10)
    )
    val tasbeehCounts: StateFlow<Map<String, Int>> = _tasbeehCounts.asStateFlow()

    private val _quranBookmarks = MutableStateFlow<List<QuranBookmark>>(
        listOf(
            QuranBookmark(1, "আল-ফাতিহা", 1),
            QuranBookmark(36, "ইয়াসীন", 1)
        )
    )
    val quranBookmarks: StateFlow<List<QuranBookmark>> = _quranBookmarks.asStateFlow()

    private val _eventReminders = MutableStateFlow<Set<String>>(setOf("ev_1"))
    val eventReminders: StateFlow<Set<String>> = _eventReminders.asStateFlow()

    private val _donationHistory = MutableStateFlow<List<DonationRecord>>(
        listOf(
            DonationRecord(
                id = "don_101",
                fundTitle = "সাধারণ মসজিদ তহবিল",
                amount = 1000,
                paymentMethod = "bKash (বিকাশ)",
                transactionId = "TXN98726143",
                donorName = "আব্দুর রহমান",
                donorPhone = "017XXXXXXXX",
                dateString = "০৫ মে, ২০২৫",
                status = "সফলভাবে গৃহীত"
            ),
            DonationRecord(
                id = "don_102",
                fundTitle = "নির্মাণ ও সংস্কার তহবিল",
                amount = 5000,
                paymentMethod = "ইসলামী ব্যাংক (অনলাইন ট্রান্সফার)",
                transactionId = "IBBL20250501",
                donorName = "মুহিব্বুল্লাহ",
                donorPhone = "018XXXXXXXX",
                dateString = "০১ মে, ২০২৫",
                status = "সফলভাবে গৃহীত"
            )
        )
    )
    val donationHistory: StateFlow<List<DonationRecord>> = _donationHistory.asStateFlow()

    fun initialize(context: Context) {
        val repo = OfflinePrayerRepository.getInstance(context)
        offlineRepo = repo
        coroutineScope.launch {
            repo.initializeDatabase()
            repo.settingsFlow.collectLatest { persistedSettings ->
                _settings.value = persistedSettings
            }
        }
    }

    // -------------------------------------------------------------
    // ACTIONS
    // -------------------------------------------------------------
    fun updateSettings(newSettings: AppSettings) {
        _settings.value = newSettings
        coroutineScope.launch {
            offlineRepo?.saveSettings(newSettings)
        }
    }

    fun toggleDuaBookmark(duaId: String) {
        _bookmarkedDuaIds.update { current ->
            if (current.contains(duaId)) current - duaId else current + duaId
        }
    }

    fun markNotificationAsRead(notifId: String) {
        _readNotificationIds.update { it + notifId }
    }

    fun markAllNotificationsAsRead(allIds: List<String>) {
        _readNotificationIds.update { it + allIds }
    }

    fun clearReadNotifications() {
        _readNotificationIds.value = emptySet()
    }

    fun incrementTasbeeh(tasbeehId: String) {
        _tasbeehCounts.update { current ->
            val count = current[tasbeehId] ?: 0
            current + (tasbeehId to (count + 1))
        }
    }

    fun incrementTasbeehCount(tasbeehId: String) = incrementTasbeeh(tasbeehId)

    fun resetTasbeeh(tasbeehId: String) {
        _tasbeehCounts.update { current ->
            current + (tasbeehId to 0)
        }
    }

    fun resetTasbeehCount(tasbeehId: String) = resetTasbeeh(tasbeehId)

    fun getTasbeehCount(tasbeehId: String): Int = _tasbeehCounts.value[tasbeehId] ?: 0

    fun isDuaBookmarked(duaId: String): Boolean = _bookmarkedDuaIds.value.contains(duaId)

    fun toggleEventReminder(eventId: String) {
        _eventReminders.update { current ->
            if (current.contains(eventId)) current - eventId else current + eventId
        }
    }

    fun addDonationRecord(record: DonationRecord) {
        _donationHistory.update { listOf(record) + it }
    }

    fun saveQuranBookmark(bookmark: QuranBookmark) {
        _quranBookmarks.update { current ->
            val filtered = current.filterNot { it.surahNumber == bookmark.surahNumber }
            listOf(bookmark) + filtered
        }
    }
}

