package com.robiul.mosquetime.feature.admin.emergency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robiul.mosquetime.core.auth.AdminUser
import com.robiul.mosquetime.core.auth.PermissionManager
import com.robiul.mosquetime.data.firebase.MosqueAdminRepository
import com.robiul.mosquetime.data.model.AppNotification
import com.robiul.mosquetime.data.model.EmergencyAlert
import com.robiul.mosquetime.data.model.JanazaNotice
import com.robiul.mosquetime.data.model.NotificationCategory
import com.robiul.mosquetime.data.repository.MosqueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

sealed interface AdminEmergencyUiState {
    object Loading : AdminEmergencyUiState
    data class Success(
        val janazaList: List<JanazaNotice>,
        val emergencyAlerts: List<EmergencyAlert>,
        val canEdit: Boolean
    ) : AdminEmergencyUiState
    data class Error(val message: String) : AdminEmergencyUiState
}

@HiltViewModel
class AdminEmergencyViewModel @Inject constructor(
    private val adminRepo: MosqueAdminRepository,
    private val mosqueRepository: MosqueRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<AdminUser?>(null)
    val currentUser: StateFlow<AdminUser?> = _currentUser.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    val uiState: StateFlow<AdminEmergencyUiState> = combine(
        mosqueRepository.janazaNoticesFlow,
        mosqueRepository.emergencyAlertsFlow,
        _currentUser
    ) { janazaList, alerts, user ->
        val canEdit = user == null || PermissionManager.canSendEmergencyAnnouncement(user)

        AdminEmergencyUiState.Success(
            janazaList = janazaList,
            emergencyAlerts = alerts,
            canEdit = canEdit
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AdminEmergencyUiState.Loading
    )

    fun setCurrentAdmin(user: AdminUser?) {
        _currentUser.value = user
    }

    fun saveJanazaNotice(
        id: String?,
        deceasedName: String,
        deceasedAge: String,
        residence: String,
        demiseTime: String,
        janazaTime: String,
        janazaLocation: String,
        imamName: String,
        graveyard: String,
        contactPhone: String,
        specialMessage: String,
        broadcastToMusallis: Boolean
    ) {
        viewModelScope.launch {
            val janazaId = id ?: ("janaza_" + UUID.randomUUID().toString().take(8))
            val notice = JanazaNotice(
                id = janazaId,
                deceasedNameBn = deceasedName,
                deceasedAge = deceasedAge,
                residenceBn = residence,
                demiseTimeBn = demiseTime,
                janazaTimeBn = janazaTime,
                janazaLocationBn = janazaLocation,
                imamNameBn = imamName,
                graveyardBn = graveyard,
                contactFamilyPhone = contactPhone,
                specialMessageBn = specialMessage,
                timestamp = System.currentTimeMillis()
            )

            adminRepo.saveJanazaNotice(notice, _currentUser.value)

            if (broadcastToMusallis) {
                val notif = AppNotification(
                    id = "notif_janaza_" + System.currentTimeMillis(),
                    title = "জানাজার জরুরি ঘোষণা: $deceasedName",
                    message = "জানাজার সময়: $janazaTime, স্থান: $janazaLocation। সকল মুসল্লিদের জানাজায় শরিক হওয়ার অনুরোধ করা হচ্ছে।",
                    timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    timeAgo = "এইমাত্র",
                    category = NotificationCategory.SPECIAL,
                    isRead = false,
                    targetRoute = "emergency"
                )
                adminRepo.broadcastNotification(notif, _currentUser.value)
            }

            _actionMessage.value = if (id != null) "জানাজার তথ্য আপডেট করা হয়েছে" else "জানাজার জরুরি বিজ্ঞপ্তি সফলভাবে সম্প্রচার করা হয়েছে"
        }
    }

    fun deleteJanaza(janazaId: String, name: String) {
        viewModelScope.launch {
            adminRepo.deleteJanazaNotice(janazaId, name, _currentUser.value)
            _actionMessage.value = "জানাজার বিজ্ঞপ্তি মুছে ফেলা হয়েছে"
        }
    }

    fun saveEmergencyAlert(
        id: String?,
        title: String,
        category: String,
        description: String,
        urgency: String,
        contactPerson: String,
        contactPhone: String,
        broadcastToMusallis: Boolean
    ) {
        viewModelScope.launch {
            val alertId = id ?: ("em_" + UUID.randomUUID().toString().take(8))
            val dateStr = SimpleDateFormat("d MMMM, yyyy", Locale.forLanguageTag("bn-BD")).format(Date())

            val alert = EmergencyAlert(
                id = alertId,
                titleBn = title,
                categoryBn = category,
                descriptionBn = description,
                urgencyLevel = urgency,
                contactPerson = contactPerson,
                contactPhone = contactPhone,
                dateBn = dateStr,
                isResolved = false
            )

            adminRepo.saveEmergencyAlert(alert, _currentUser.value)

            if (broadcastToMusallis) {
                val notif = AppNotification(
                    id = "notif_em_" + System.currentTimeMillis(),
                    title = "জরুরি আবেদন: $title",
                    message = "$description | যোগাযোগ: $contactPhone",
                    timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    timeAgo = "এইমাত্র",
                    category = NotificationCategory.SPECIAL,
                    isRead = false,
                    targetRoute = "emergency"
                )
                adminRepo.broadcastNotification(notif, _currentUser.value)
            }

            _actionMessage.value = if (id != null) "জরুরি বিজ্ঞপ্তি আপডেট করা হয়েছে" else "জরুরি বিজ্ঞপ্তি ও পুশ নোটিফিকেশন পাঠানো হয়েছে"
        }
    }

    fun toggleEmergencyAlertResolved(alert: EmergencyAlert) {
        viewModelScope.launch {
            val updated = alert.copy(isResolved = !alert.isResolved)
            adminRepo.saveEmergencyAlert(updated, _currentUser.value)
            _actionMessage.value = if (updated.isResolved) "বিজ্ঞপ্তিটি 'সমাধান সম্পন্ন' হিসেবে চিহ্নিত হয়েছে" else "বিজ্ঞপ্তিটি আবার সক্রিয় করা হয়েছে"
        }
    }

    fun deleteEmergencyAlert(alertId: String, title: String) {
        viewModelScope.launch {
            adminRepo.deleteEmergencyAlert(alertId, title, _currentUser.value)
            _actionMessage.value = "জরুরি বিজ্ঞপ্তি মুছে ফেলা হয়েছে"
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}
