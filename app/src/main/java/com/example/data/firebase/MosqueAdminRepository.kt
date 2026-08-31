package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.core.auth.AdminUser
import com.example.data.model.CommitteeCategory
import com.example.data.model.CommitteeMember
import com.example.data.model.AppNotification
import com.example.data.model.EmergencyAlert
import com.example.data.model.ExtraPrayerTime
import com.example.data.model.FacilityItem
import com.example.data.model.JanazaNotice
import com.example.data.model.MosqueDetails
import com.example.data.model.NoticeCategory
import com.example.data.model.NoticeItem
import com.example.data.model.NotificationCategory
import com.example.data.model.PrayerTimeItem
import com.example.data.repository.MosqueRepository
import com.example.ui.components.PrayerType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CustomPrayerOverride(
    val fajrAzan: String = "04:35",
    val fajrIqamah: String = "05:05",
    val dhuhrAzan: String = "12:00",
    val dhuhrIqamah: String = "01:15",
    val asrAzan: String = "04:30",
    val asrIqamah: String = "05:00",
    val maghribAzan: String = "06:35",
    val maghribIqamah: String = "06:45",
    val ishaAzan: String = "07:55",
    val ishaIqamah: String = "08:30",
    val jumahAzan1: String = "12:45",
    val jumahKhutbah: String = "01:15",
    val jumahJamath: String = "01:30",
    val sehriEnd: String = "03:58",
    val iftarTime: String = "06:36",
    val tahajjudTime: String = "01:30 - 03:45",
    val ishraqTime: String = "05:22",
    val chashtTime: String = "08:15 - 11:15",
    val isCustomScheduleActive: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis(),
    val updatedBy: String = "Admin"
)

class MosqueAdminRepository private constructor(
    private val firestore: FirebaseFirestore = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null } ?: FirebaseFirestore.getInstance()
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _mosqueDetails = MutableStateFlow<MosqueDetails>(MosqueRepository.mosqueInfo)
    val mosqueDetails: StateFlow<MosqueDetails> = _mosqueDetails.asStateFlow()

    private val _prayerOverrides = MutableStateFlow(CustomPrayerOverride())
    val prayerOverrides: StateFlow<CustomPrayerOverride> = _prayerOverrides.asStateFlow()

    private val _committeeList = MutableStateFlow<List<CommitteeMember>>(MosqueRepository.committeeMembers)
    val committeeList: StateFlow<List<CommitteeMember>> = _committeeList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchInitialData()
    }

    private fun fetchInitialData() {
        scope.launch {
            loadMosqueProfileFromFirestore()
            loadPrayerScheduleFromFirestore()
        }
    }

    suspend fun loadMosqueProfileFromFirestore() = withContext(Dispatchers.IO) {
        try {
            val doc = firestore.document(FirestoreCollections.profileDoc()).get().await()
            if (doc != null && doc.exists()) {
                val nameBn = doc.getString("nameBn") ?: MosqueRepository.mosqueInfo.nameBn
                val nameEn = doc.getString("nameEn") ?: MosqueRepository.mosqueInfo.nameEn
                val establishedYear = doc.getString("establishedYear") ?: MosqueRepository.mosqueInfo.establishedYear
                val address = doc.getString("address") ?: MosqueRepository.mosqueInfo.address
                val district = doc.getString("district") ?: MosqueRepository.mosqueInfo.district
                val capacity = doc.getString("capacity") ?: MosqueRepository.mosqueInfo.capacity
                val floors = doc.getString("floors") ?: MosqueRepository.mosqueInfo.floors
                val history = doc.getString("history") ?: MosqueRepository.mosqueInfo.history
                val description = doc.getString("description") ?: MosqueRepository.mosqueInfo.description
                val imamName = doc.getString("imamName") ?: MosqueRepository.mosqueInfo.imamName
                val imamTitle = doc.getString("imamTitle") ?: MosqueRepository.mosqueInfo.imamTitle
                val imamEducation = doc.getString("imamEducation") ?: MosqueRepository.mosqueInfo.imamEducation
                val imamPhone = doc.getString("imamPhone") ?: MosqueRepository.mosqueInfo.imamPhone
                val muazzinName = doc.getString("muazzinName") ?: MosqueRepository.mosqueInfo.muazzinName
                val muazzinPhone = doc.getString("muazzinPhone") ?: MosqueRepository.mosqueInfo.muazzinPhone
                val khademName = doc.getString("khademName") ?: MosqueRepository.mosqueInfo.khademName
                val officePhone = doc.getString("officePhone") ?: MosqueRepository.mosqueInfo.officePhone
                val officeEmail = doc.getString("officeEmail") ?: MosqueRepository.mosqueInfo.officeEmail
                val website = doc.getString("website") ?: MosqueRepository.mosqueInfo.website

                val facilitiesData = doc.get("facilities") as? List<Map<String, Any>>
                val facilities = if (facilitiesData != null) {
                    facilitiesData.map {
                        FacilityItem(
                            title = it["title"] as? String ?: "",
                            description = it["description"] as? String ?: "",
                            iconType = it["iconType"] as? String ?: "ac"
                        )
                    }
                } else {
                    MosqueRepository.mosqueInfo.facilities
                }

                val loadedDetails = MosqueDetails(
                    nameBn = nameBn,
                    nameEn = nameEn,
                    establishedYear = establishedYear,
                    address = address,
                    district = district,
                    capacity = capacity,
                    floors = floors,
                    history = history,
                    description = description,
                    imamName = imamName,
                    imamTitle = imamTitle,
                    imamEducation = imamEducation,
                    imamPhone = imamPhone,
                    muazzinName = muazzinName,
                    muazzinPhone = muazzinPhone,
                    khademName = khademName,
                    officePhone = officePhone,
                    officeEmail = officeEmail,
                    website = website,
                    facilities = facilities
                )

                _mosqueDetails.value = loadedDetails
                MosqueRepository.updateMosqueInfo(loadedDetails)
            }
        } catch (e: Exception) {
            Log.e("MosqueAdminRepo", "Error fetching mosque profile: ${e.message}")
        }
    }

    suspend fun saveMosqueProfile(
        details: MosqueDetails,
        adminUser: AdminUser? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        _isLoading.value = true
        try {
            // 1. Update in-memory public repository
            _mosqueDetails.value = details
            MosqueRepository.updateMosqueInfo(details)

            // 2. Sync to Firebase Firestore
            val facilitiesMap = details.facilities.map {
                mapOf("title" to it.title, "description" to it.description, "iconType" to it.iconType)
            }

            val data = hashMapOf(
                "nameBn" to details.nameBn,
                "nameEn" to details.nameEn,
                "establishedYear" to details.establishedYear,
                "address" to details.address,
                "district" to details.district,
                "capacity" to details.capacity,
                "floors" to details.floors,
                "history" to details.history,
                "description" to details.description,
                "imamName" to details.imamName,
                "imamTitle" to details.imamTitle,
                "imamEducation" to details.imamEducation,
                "imamPhone" to details.imamPhone,
                "muazzinName" to details.muazzinName,
                "muazzinPhone" to details.muazzinPhone,
                "khademName" to details.khademName,
                "officePhone" to details.officePhone,
                "officeEmail" to details.officeEmail,
                "website" to details.website,
                "facilities" to facilitiesMap,
                "lastUpdated" to System.currentTimeMillis(),
                "updatedBy" to (adminUser?.nameBn ?: "Super Admin")
            )

            firestore.document(FirestoreCollections.profileDoc())
                .set(data, SetOptions.merge())
                .await()

            // 3. Log Audit
            logAudit(
                adminUser = adminUser,
                action = "UPDATE_MOSQUE_PROFILE",
                target = "Mosque Profile Info",
                details = "Updated details for ${details.nameBn}"
            )

            _isLoading.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            Log.e("MosqueAdminRepo", "Save Mosque Profile error: ${e.message}")
            // Still successful locally if network fails
            Result.success(Unit)
        }
    }

    suspend fun loadPrayerScheduleFromFirestore() = withContext(Dispatchers.IO) {
        try {
            val doc = firestore.document(FirestoreCollections.prayersDoc()).get().await()
            if (doc != null && doc.exists()) {
                val override = CustomPrayerOverride(
                    fajrAzan = doc.getString("fajrAzan") ?: "04:35",
                    fajrIqamah = doc.getString("fajrIqamah") ?: "05:05",
                    dhuhrAzan = doc.getString("dhuhrAzan") ?: "12:00",
                    dhuhrIqamah = doc.getString("dhuhrIqamah") ?: "01:15",
                    asrAzan = doc.getString("asrAzan") ?: "04:30",
                    asrIqamah = doc.getString("asrIqamah") ?: "05:00",
                    maghribAzan = doc.getString("maghribAzan") ?: "06:35",
                    maghribIqamah = doc.getString("maghribIqamah") ?: "06:45",
                    ishaAzan = doc.getString("ishaAzan") ?: "07:55",
                    ishaIqamah = doc.getString("ishaIqamah") ?: "08:30",
                    jumahAzan1 = doc.getString("jumahAzan1") ?: "12:45",
                    jumahKhutbah = doc.getString("jumahKhutbah") ?: "01:15",
                    jumahJamath = doc.getString("jumahJamath") ?: "01:30",
                    sehriEnd = doc.getString("sehriEnd") ?: "03:58",
                    iftarTime = doc.getString("iftarTime") ?: "06:36",
                    tahajjudTime = doc.getString("tahajjudTime") ?: "01:30 - 03:45",
                    ishraqTime = doc.getString("ishraqTime") ?: "05:22",
                    chashtTime = doc.getString("chashtTime") ?: "08:15 - 11:15",
                    isCustomScheduleActive = doc.getBoolean("isCustomScheduleActive") ?: true,
                    lastUpdated = doc.getLong("lastUpdated") ?: System.currentTimeMillis(),
                    updatedBy = doc.getString("updatedBy") ?: "Admin"
                )
                _prayerOverrides.value = override
                MosqueRepository.setCustomPrayerOverrides(override)
            }
        } catch (e: Exception) {
            Log.e("MosqueAdminRepo", "Error fetching prayer schedule: ${e.message}")
        }
    }

    suspend fun savePrayerSchedule(
        overrides: CustomPrayerOverride,
        adminUser: AdminUser? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        _isLoading.value = true
        try {
            // 1. Update in-memory public repository
            _prayerOverrides.value = overrides
            MosqueRepository.setCustomPrayerOverrides(overrides)

            // 2. Save to Firestore
            val data = hashMapOf(
                "fajrAzan" to overrides.fajrAzan,
                "fajrIqamah" to overrides.fajrIqamah,
                "dhuhrAzan" to overrides.dhuhrAzan,
                "dhuhrIqamah" to overrides.dhuhrIqamah,
                "asrAzan" to overrides.asrAzan,
                "asrIqamah" to overrides.asrIqamah,
                "maghribAzan" to overrides.maghribAzan,
                "maghribIqamah" to overrides.maghribIqamah,
                "ishaAzan" to overrides.ishaAzan,
                "ishaIqamah" to overrides.ishaIqamah,
                "jumahAzan1" to overrides.jumahAzan1,
                "jumahKhutbah" to overrides.jumahKhutbah,
                "jumahJamath" to overrides.jumahJamath,
                "sehriEnd" to overrides.sehriEnd,
                "iftarTime" to overrides.iftarTime,
                "tahajjudTime" to overrides.tahajjudTime,
                "ishraqTime" to overrides.ishraqTime,
                "chashtTime" to overrides.chashtTime,
                "isCustomScheduleActive" to overrides.isCustomScheduleActive,
                "lastUpdated" to System.currentTimeMillis(),
                "updatedBy" to (adminUser?.nameBn ?: "Super Admin")
            )

            firestore.document(FirestoreCollections.prayersDoc())
                .set(data, SetOptions.merge())
                .await()

            // 3. Log Audit
            logAudit(
                adminUser = adminUser,
                action = "UPDATE_PRAYER_SCHEDULE",
                target = "Daily Prayer Timetable",
                details = "Updated Azan & Iqamah timings"
            )

            _isLoading.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            Log.e("MosqueAdminRepo", "Save Prayer Schedule error: ${e.message}")
            Result.success(Unit)
        }
    }

    suspend fun saveCommitteeMembers(
        members: List<CommitteeMember>,
        adminUser: AdminUser? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        _isLoading.value = true
        try {
            _committeeList.value = members
            MosqueRepository.updateCommitteeMembers(members)

            val memberMaps = members.map {
                mapOf(
                    "id" to it.id,
                    "name" to it.name,
                    "designationBn" to it.designationBn,
                    "category" to it.category.name,
                    "phone" to it.phone,
                    "profession" to it.profession,
                    "termYears" to it.termYears
                )
            }

            val data = hashMapOf(
                "members" to memberMaps,
                "lastUpdated" to System.currentTimeMillis(),
                "updatedBy" to (adminUser?.nameBn ?: "Admin")
            )

            firestore.document("${FirestoreCollections.DEFAULT_MOSQUE_ID}/${FirestoreCollections.COMMITTEE}/list")
                .set(data, SetOptions.merge())
                .await()

            logAudit(
                adminUser = adminUser,
                action = "UPDATE_COMMITTEE",
                target = "Committee Members",
                details = "Updated ${members.size} committee members"
            )

            _isLoading.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            Log.e("MosqueAdminRepo", "Save Committee error: ${e.message}")
            Result.success(Unit)
        }
    }

    // -------------------------------------------------------------
    // NOTICES MANAGEMENT
    // -------------------------------------------------------------
    suspend fun saveNotice(notice: NoticeItem, adminUser: AdminUser? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _isLoading.value = true
            MosqueRepository.addOrUpdateNotice(notice)

            val data = hashMapOf(
                "id" to notice.id,
                "title" to notice.title,
                "summary" to notice.summary,
                "fullContent" to notice.fullContent,
                "category" to notice.category.name,
                "publishedDate" to notice.publishedDate,
                "isPinned" to notice.isPinned,
                "author" to notice.author,
                "attachmentUrl" to (notice.attachmentUrl ?: ""),
                "updatedAt" to System.currentTimeMillis()
            )

            firestore.document("${FirestoreCollections.noticesCollection()}/${notice.id}")
                .set(data, SetOptions.merge())
                .await()

            logAudit(
                adminUser = adminUser,
                action = "SAVE_NOTICE",
                target = "Notice: ${notice.title}",
                details = "Category: ${notice.category.name}, Pinned: ${notice.isPinned}"
            )

            _isLoading.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            Log.e("MosqueAdminRepo", "Save notice error: ${e.message}")
            Result.success(Unit)
        }
    }

    suspend fun deleteNotice(noticeId: String, title: String = "", adminUser: AdminUser? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _isLoading.value = true
            MosqueRepository.deleteNotice(noticeId)

            firestore.document("${FirestoreCollections.noticesCollection()}/$noticeId")
                .delete()
                .await()

            logAudit(
                adminUser = adminUser,
                action = "DELETE_NOTICE",
                target = "Notice ID: $noticeId",
                details = "Deleted notice: $title"
            )

            _isLoading.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            Log.e("MosqueAdminRepo", "Delete notice error: ${e.message}")
            Result.success(Unit)
        }
    }

    // -------------------------------------------------------------
    // JANAZA & EMERGENCY MANAGEMENT
    // -------------------------------------------------------------
    suspend fun saveJanazaNotice(janaza: JanazaNotice, adminUser: AdminUser? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _isLoading.value = true
            MosqueRepository.addOrUpdateJanaza(janaza)

            val data = hashMapOf(
                "id" to janaza.id,
                "deceasedNameBn" to janaza.deceasedNameBn,
                "deceasedAge" to janaza.deceasedAge,
                "residenceBn" to janaza.residenceBn,
                "demiseTimeBn" to janaza.demiseTimeBn,
                "janazaTimeBn" to janaza.janazaTimeBn,
                "janazaLocationBn" to janaza.janazaLocationBn,
                "imamNameBn" to janaza.imamNameBn,
                "graveyardBn" to janaza.graveyardBn,
                "contactFamilyPhone" to janaza.contactFamilyPhone,
                "specialMessageBn" to janaza.specialMessageBn,
                "timestamp" to janaza.timestamp
            )

            firestore.document("${FirestoreCollections.janazaCollection()}/${janaza.id}")
                .set(data, SetOptions.merge())
                .await()

            logAudit(
                adminUser = adminUser,
                action = "SAVE_JANAZA",
                target = "Janaza: ${janaza.deceasedNameBn}",
                details = "Time: ${janaza.janazaTimeBn}, Location: ${janaza.janazaLocationBn}"
            )

            _isLoading.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            Log.e("MosqueAdminRepo", "Save Janaza error: ${e.message}")
            Result.success(Unit)
        }
    }

    suspend fun deleteJanazaNotice(janazaId: String, name: String = "", adminUser: AdminUser? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _isLoading.value = true
            MosqueRepository.deleteJanaza(janazaId)

            firestore.document("${FirestoreCollections.janazaCollection()}/$janazaId")
                .delete()
                .await()

            logAudit(
                adminUser = adminUser,
                action = "DELETE_JANAZA",
                target = "Janaza ID: $janazaId",
                details = "Deleted Janaza for: $name"
            )

            _isLoading.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            Log.e("MosqueAdminRepo", "Delete Janaza error: ${e.message}")
            Result.success(Unit)
        }
    }

    suspend fun saveEmergencyAlert(alert: EmergencyAlert, adminUser: AdminUser? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _isLoading.value = true
            MosqueRepository.addOrUpdateEmergencyAlert(alert)

            val data = hashMapOf(
                "id" to alert.id,
                "titleBn" to alert.titleBn,
                "categoryBn" to alert.categoryBn,
                "descriptionBn" to alert.descriptionBn,
                "urgencyLevel" to alert.urgencyLevel,
                "contactPerson" to alert.contactPerson,
                "contactPhone" to alert.contactPhone,
                "dateBn" to alert.dateBn,
                "isResolved" to alert.isResolved
            )

            firestore.document("${FirestoreCollections.emergencyCollection()}/${alert.id}")
                .set(data, SetOptions.merge())
                .await()

            logAudit(
                adminUser = adminUser,
                action = "SAVE_EMERGENCY_ALERT",
                target = "Alert: ${alert.titleBn}",
                details = "Urgency: ${alert.urgencyLevel}, Contact: ${alert.contactPhone}"
            )

            _isLoading.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            Log.e("MosqueAdminRepo", "Save Emergency Alert error: ${e.message}")
            Result.success(Unit)
        }
    }

    suspend fun deleteEmergencyAlert(alertId: String, title: String = "", adminUser: AdminUser? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _isLoading.value = true
            MosqueRepository.deleteEmergencyAlert(alertId)

            firestore.document("${FirestoreCollections.emergencyCollection()}/$alertId")
                .delete()
                .await()

            logAudit(
                adminUser = adminUser,
                action = "DELETE_EMERGENCY_ALERT",
                target = "Alert ID: $alertId",
                details = "Deleted Alert: $title"
            )

            _isLoading.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            Log.e("MosqueAdminRepo", "Delete Emergency Alert error: ${e.message}")
            Result.success(Unit)
        }
    }

    // -------------------------------------------------------------
    // BROADCAST NOTIFICATIONS
    // -------------------------------------------------------------
    suspend fun broadcastNotification(notification: AppNotification, adminUser: AdminUser? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _isLoading.value = true
            MosqueRepository.broadcastNotification(notification)

            val data = hashMapOf(
                "id" to notification.id,
                "title" to notification.title,
                "message" to notification.message,
                "timestamp" to notification.timestamp,
                "timeAgo" to notification.timeAgo,
                "category" to notification.category.name,
                "isRead" to notification.isRead,
                "targetRoute" to (notification.targetRoute ?: "")
            )

            firestore.document("${FirestoreCollections.notificationsCollection()}/${notification.id}")
                .set(data, SetOptions.merge())
                .await()

            logAudit(
                adminUser = adminUser,
                action = "BROADCAST_NOTIFICATION",
                target = "Notification: ${notification.title}",
                details = "Category: ${notification.category.name}, Message: ${notification.message}"
            )

            _isLoading.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoading.value = false
            Log.e("MosqueAdminRepo", "Broadcast notification error: ${e.message}")
            Result.success(Unit)
        }
    }

    private suspend fun logAudit(
        adminUser: AdminUser?,
        action: String,
        target: String,
        details: String
    ) {
        try {
            val logData = hashMapOf(
                "timestamp" to System.currentTimeMillis(),
                "formattedTime" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                "userId" to (adminUser?.uid ?: "local_admin"),
                "userName" to (adminUser?.nameBn ?: "অ্যাডমিন"),
                "role" to (adminUser?.role?.name ?: "ADMIN"),
                "action" to action,
                "target" to target,
                "details" to details
            )
            firestore.collection(FirestoreCollections.AUDIT_LOGS).add(logData).await()
        } catch (e: Exception) {
            Log.w("MosqueAdminRepo", "Audit log ignored offline: ${e.message}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MosqueAdminRepository? = null

        fun getInstance(): MosqueAdminRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = MosqueAdminRepository()
                INSTANCE = instance
                instance
            }
        }
    }
}
