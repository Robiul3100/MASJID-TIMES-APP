package com.example.feature.admin.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.auth.AdminUser
import com.example.core.auth.PermissionManager
import com.example.data.firebase.MosqueAdminRepository
import com.example.data.model.AppNotification
import com.example.data.model.NotificationCategory
import com.example.data.repository.MosqueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
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

sealed interface AdminNotificationUiState {
    object Loading : AdminNotificationUiState
    data class Success(
        val notifications: List<AppNotification>,
        val canEdit: Boolean
    ) : AdminNotificationUiState
    data class Error(val message: String) : AdminNotificationUiState
}

@HiltViewModel
class AdminNotificationViewModel @Inject constructor(
    private val adminRepo: MosqueAdminRepository,
    private val mosqueRepository: MosqueRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<AdminUser?>(null)
    val currentUser: StateFlow<AdminUser?> = _currentUser.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    val uiState: StateFlow<AdminNotificationUiState> = combine(
        mosqueRepository.notificationsFlow,
        _currentUser
    ) { notifications, user ->
        val canEdit = user == null || PermissionManager.canSendNotifications(user)

        AdminNotificationUiState.Success(
            notifications = notifications,
            canEdit = canEdit
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AdminNotificationUiState.Loading
    )

    fun setCurrentAdmin(user: AdminUser?) {
        _currentUser.value = user
    }

    fun broadcastCustomNotification(
        title: String,
        message: String,
        category: NotificationCategory,
        targetRoute: String
    ) {
        viewModelScope.launch {
            val nowTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val notif = AppNotification(
                id = "broadcast_" + UUID.randomUUID().toString().take(8),
                title = title,
                message = message,
                timestamp = "আজ, $nowTime",
                timeAgo = "এইমাত্র",
                category = category,
                isRead = false,
                targetRoute = targetRoute
            )

            adminRepo.broadcastNotification(notif, _currentUser.value)
            _actionMessage.value = "নোটিফিকেশন সফলভাবে সম্প্রচার করা হয়েছে"
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            mosqueRepository.clearAllNotifications()
            _actionMessage.value = "সকল নোটিফিকেশন হিস্ট্রি মুছে ফেলা হয়েছে"
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}
