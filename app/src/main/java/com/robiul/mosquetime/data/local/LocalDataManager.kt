package com.robiul.mosquetime.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.robiul.mosquetime.MosqueApplication
import com.robiul.mosquetime.core.auth.AdminUser
import com.robiul.mosquetime.data.firebase.CustomPrayerOverride
import com.robiul.mosquetime.data.firebase.MosqueConfig
import com.robiul.mosquetime.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataManager @Inject constructor() {

    private val prefs: SharedPreferences by lazy {
        try {
            MosqueApplication.appContext.getSharedPreferences("masjid_times_local_prefs", Context.MODE_PRIVATE)
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Failed to access shared preferences: ${e.message}")
            MosqueApplication.instance.getSharedPreferences("masjid_times_local_prefs", Context.MODE_PRIVATE)
        }
    }

    private val gson = Gson()

    companion object {
        @Volatile
        private var instance: LocalDataManager? = null

        fun getInstance(): LocalDataManager {
            return instance ?: synchronized(this) {
                instance ?: LocalDataManager().also { instance = it }
            }
        }

        private const val KEY_CONFIGURED_MOSQUES = "configured_mosques_list"
        private const val KEY_ACTIVE_MOSQUE_ID = "active_mosque_id"
        private const val KEY_ADMIN_USERS = "admin_users_list"
        private const val KEY_PREFIX_PRAYER_OVERRIDE = "prayer_override_"
        private const val KEY_PREFIX_MOSQUE_DETAILS = "mosque_details_"
        private const val KEY_PREFIX_COMMITTEE = "committee_"
        private const val KEY_PREFIX_NOTICES = "notices_"
        private const val KEY_PREFIX_EMERGENCY = "emergency_"
        private const val KEY_PREFIX_MEAL_SCHEDULE = "meal_schedule_"
        private const val KEY_PREFIX_HOUSEHOLDS = "households_"
        private const val KEY_PREFIX_DONATIONS = "donations_"
        private const val KEY_PREFIX_BANK_ACCOUNTS = "bank_accounts_"
        private const val KEY_PREFIX_MOBILE_ACCOUNTS = "mobile_accounts_"
        private const val KEY_PREFIX_RAMADAN = "ramadan_days_"
    }

    // 1. Configured Mosques
    fun saveConfiguredMosques(list: List<MosqueConfig>) {
        try {
            val json = gson.toJson(list)
            prefs.edit().putString(KEY_CONFIGURED_MOSQUES, json).apply()
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error saving configured mosques: ${e.message}")
        }
    }

    fun getConfiguredMosques(): List<MosqueConfig>? {
        return try {
            val json = prefs.getString(KEY_CONFIGURED_MOSQUES, null) ?: return null
            val type = object : TypeToken<List<MosqueConfig>>() {}.type
            gson.fromJson<List<MosqueConfig>>(json, type)
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error loading configured mosques: ${e.message}")
            null
        }
    }

    // 2. Active Mosque ID
    fun saveActiveMosqueId(id: String) {
        prefs.edit().putString(KEY_ACTIVE_MOSQUE_ID, id).apply()
    }

    fun getActiveMosqueId(): String? {
        return prefs.getString(KEY_ACTIVE_MOSQUE_ID, null)
    }

    // 3. Admin Users
    fun saveAdminUsers(list: List<AdminUser>) {
        try {
            val json = gson.toJson(list)
            prefs.edit().putString(KEY_ADMIN_USERS, json).apply()
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error saving admin users: ${e.message}")
        }
    }

    fun getAdminUsers(): List<AdminUser>? {
        return try {
            val json = prefs.getString(KEY_ADMIN_USERS, null) ?: return null
            val type = object : TypeToken<List<AdminUser>>() {}.type
            gson.fromJson<List<AdminUser>>(json, type)
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error loading admin users: ${e.message}")
            null
        }
    }

    // 4. Prayer Overrides per Mosque
    fun savePrayerOverride(mosqueId: String, override: CustomPrayerOverride) {
        try {
            val json = gson.toJson(override)
            prefs.edit().putString(KEY_PREFIX_PRAYER_OVERRIDE + mosqueId, json).apply()
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error saving prayer override: ${e.message}")
        }
    }

    fun getPrayerOverride(mosqueId: String): CustomPrayerOverride? {
        return try {
            val json = prefs.getString(KEY_PREFIX_PRAYER_OVERRIDE + mosqueId, null) ?: return null
            gson.fromJson(json, CustomPrayerOverride::class.java)
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error loading prayer override: ${e.message}")
            null
        }
    }

    // 5. Mosque Details per Mosque
    fun saveMosqueDetails(mosqueId: String, details: MosqueDetails) {
        try {
            val json = gson.toJson(details)
            prefs.edit().putString(KEY_PREFIX_MOSQUE_DETAILS + mosqueId, json).apply()
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error saving mosque details: ${e.message}")
        }
    }

    fun getMosqueDetails(mosqueId: String): MosqueDetails? {
        return try {
            val json = prefs.getString(KEY_PREFIX_MOSQUE_DETAILS + mosqueId, null) ?: return null
            gson.fromJson(json, MosqueDetails::class.java)
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error loading mosque details: ${e.message}")
            null
        }
    }

    // 6. Committee List per Mosque
    fun saveCommitteeList(mosqueId: String, list: List<CommitteeMember>) {
        try {
            val json = gson.toJson(list)
            prefs.edit().putString(KEY_PREFIX_COMMITTEE + mosqueId, json).apply()
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error saving committee: ${e.message}")
        }
    }

    fun getCommitteeList(mosqueId: String): List<CommitteeMember>? {
        return try {
            val json = prefs.getString(KEY_PREFIX_COMMITTEE + mosqueId, null) ?: return null
            val type = object : TypeToken<List<CommitteeMember>>() {}.type
            gson.fromJson<List<CommitteeMember>>(json, type)
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error loading committee: ${e.message}")
            null
        }
    }

    // 7. Notices per Mosque
    fun saveNotices(mosqueId: String, list: List<NoticeItem>) {
        try {
            val json = gson.toJson(list)
            prefs.edit().putString(KEY_PREFIX_NOTICES + mosqueId, json).apply()
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error saving notices: ${e.message}")
        }
    }

    fun getNotices(mosqueId: String): List<NoticeItem>? {
        return try {
            val json = prefs.getString(KEY_PREFIX_NOTICES + mosqueId, null) ?: return null
            val type = object : TypeToken<List<NoticeItem>>() {}.type
            gson.fromJson<List<NoticeItem>>(json, type)
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error loading notices: ${e.message}")
            null
        }
    }

    // 8. Emergency Alert per Mosque
    fun saveEmergencyAlert(mosqueId: String, alert: EmergencyAlert?) {
        try {
            if (alert == null) {
                prefs.edit().remove(KEY_PREFIX_EMERGENCY + mosqueId).apply()
            } else {
                val json = gson.toJson(alert)
                prefs.edit().putString(KEY_PREFIX_EMERGENCY + mosqueId, json).apply()
            }
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error saving emergency alert: ${e.message}")
        }
    }

    fun getEmergencyAlert(mosqueId: String): EmergencyAlert? {
        return try {
            val json = prefs.getString(KEY_PREFIX_EMERGENCY + mosqueId, null) ?: return null
            gson.fromJson(json, EmergencyAlert::class.java)
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error loading emergency alert: ${e.message}")
            null
        }
    }

    // 9. Day Meal Schedule per Mosque
    fun saveMealSchedule(mosqueId: String, list: List<DayMealSchedule>) {
        try {
            val json = gson.toJson(list)
            prefs.edit().putString(KEY_PREFIX_MEAL_SCHEDULE + mosqueId, json).apply()
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error saving meal schedule: ${e.message}")
        }
    }

    fun getMealSchedule(mosqueId: String): List<DayMealSchedule>? {
        return try {
            val json = prefs.getString(KEY_PREFIX_MEAL_SCHEDULE + mosqueId, null) ?: return null
            val type = object : TypeToken<List<DayMealSchedule>>() {}.type
            gson.fromJson<List<DayMealSchedule>>(json, type)
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error loading meal schedule: ${e.message}")
            null
        }
    }

    // 10. Households roster per Mosque
    fun saveHouseholds(mosqueId: String, list: List<Household>) {
        try {
            val json = gson.toJson(list)
            prefs.edit().putString(KEY_PREFIX_HOUSEHOLDS + mosqueId, json).apply()
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error saving households: ${e.message}")
        }
    }

    fun getHouseholds(mosqueId: String): List<Household>? {
        return try {
            val json = prefs.getString(KEY_PREFIX_HOUSEHOLDS + mosqueId, null) ?: return null
            val type = object : TypeToken<List<Household>>() {}.type
            gson.fromJson<List<Household>>(json, type)
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error loading households: ${e.message}")
            null
        }
    }

    // 11. Donations per Mosque
    fun saveDonationRecords(mosqueId: String, list: List<DonationRecord>) {
        try {
            val json = gson.toJson(list)
            prefs.edit().putString(KEY_PREFIX_DONATIONS + mosqueId, json).apply()
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error saving donations: ${e.message}")
        }
    }

    fun getDonationRecords(mosqueId: String): List<DonationRecord>? {
        return try {
            val json = prefs.getString(KEY_PREFIX_DONATIONS + mosqueId, null) ?: return null
            val type = object : TypeToken<List<DonationRecord>>() {}.type
            gson.fromJson<List<DonationRecord>>(json, type)
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error loading donations: ${e.message}")
            null
        }
    }

    // 12. Bank & Mobile Accounts per Mosque
    fun saveBankAccounts(mosqueId: String, list: List<BankAccountInfo>) {
        try {
            val json = gson.toJson(list)
            prefs.edit().putString(KEY_PREFIX_BANK_ACCOUNTS + mosqueId, json).apply()
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error saving bank accounts: ${e.message}")
        }
    }

    fun getBankAccounts(mosqueId: String): List<BankAccountInfo>? {
        return try {
            val json = prefs.getString(KEY_PREFIX_BANK_ACCOUNTS + mosqueId, null) ?: return null
            val type = object : TypeToken<List<BankAccountInfo>>() {}.type
            gson.fromJson<List<BankAccountInfo>>(json, type)
        } catch (e: Exception) {
            null
        }
    }

    fun saveMobileAccounts(mosqueId: String, list: List<MobileAccountInfo>) {
        try {
            val json = gson.toJson(list)
            prefs.edit().putString(KEY_PREFIX_MOBILE_ACCOUNTS + mosqueId, json).apply()
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error saving mobile accounts: ${e.message}")
        }
    }

    fun getMobileAccounts(mosqueId: String): List<MobileAccountInfo>? {
        return try {
            val json = prefs.getString(KEY_PREFIX_MOBILE_ACCOUNTS + mosqueId, null) ?: return null
            val type = object : TypeToken<List<MobileAccountInfo>>() {}.type
            gson.fromJson<List<MobileAccountInfo>>(json, type)
        } catch (e: Exception) {
            null
        }
    }

    // 13. Ramadan Days per Mosque
    fun saveRamadanDays(mosqueId: String, list: List<RamadanDay>) {
        try {
            val json = gson.toJson(list)
            prefs.edit().putString(KEY_PREFIX_RAMADAN + mosqueId, json).apply()
        } catch (e: Exception) {
            Log.e("LocalDataManager", "Error saving ramadan days: ${e.message}")
        }
    }

    fun getRamadanDays(mosqueId: String): List<RamadanDay>? {
        return try {
            val json = prefs.getString(KEY_PREFIX_RAMADAN + mosqueId, null) ?: return null
            val type = object : TypeToken<List<RamadanDay>>() {}.type
            gson.fromJson<List<RamadanDay>>(json, type)
        } catch (e: Exception) {
            null
        }
    }
}
