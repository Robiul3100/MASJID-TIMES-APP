package com.robiul.mosquetime.feature.admin.notices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robiul.mosquetime.core.auth.AdminUser
import com.robiul.mosquetime.core.auth.PermissionManager
import com.robiul.mosquetime.data.firebase.MosqueAdminRepository
import com.robiul.mosquetime.data.model.NoticeCategory
import com.robiul.mosquetime.data.model.NoticeItem
import com.robiul.mosquetime.data.repository.MosqueRepository
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

sealed interface AdminNoticeUiState {
    object Loading : AdminNoticeUiState
    data class Success(
        val notices: List<NoticeItem>,
        val filteredNotices: List<NoticeItem>,
        val canEdit: Boolean
    ) : AdminNoticeUiState
    data class Error(val message: String) : AdminNoticeUiState
}

@HiltViewModel
class AdminNoticeViewModel @Inject constructor(
    private val adminRepo: MosqueAdminRepository,
    private val mosqueRepository: MosqueRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(NoticeCategory.ALL)
    val selectedCategory: StateFlow<NoticeCategory> = _selectedCategory.asStateFlow()

    private val _currentUser = MutableStateFlow<AdminUser?>(null)
    val currentUser: StateFlow<AdminUser?> = _currentUser.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    val uiState: StateFlow<AdminNoticeUiState> = combine(
        mosqueRepository.noticesFlow,
        _searchQuery,
        _selectedCategory,
        _currentUser
    ) { notices, query, category, user ->
        val canEdit = user == null || PermissionManager.canManageNotices(user)

        val filtered = notices.filter { notice ->
            val matchesCategory = category == NoticeCategory.ALL || notice.category == category
            val matchesQuery = query.isBlank() ||
                    notice.title.contains(query, ignoreCase = true) ||
                    notice.summary.contains(query, ignoreCase = true) ||
                    notice.author.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }

        AdminNoticeUiState.Success(
            notices = notices,
            filteredNotices = filtered,
            canEdit = canEdit
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AdminNoticeUiState.Loading
    )

    fun setCurrentAdmin(user: AdminUser?) {
        _currentUser.value = user
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: NoticeCategory) {
        _selectedCategory.value = category
    }

    fun saveNotice(
        id: String?,
        title: String,
        summary: String,
        fullContent: String,
        category: NoticeCategory,
        isPinned: Boolean,
        author: String
    ) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("d MMMM, yyyy", Locale.forLanguageTag("bn-BD")).format(Date())
            val noticeId = id ?: ("notice_" + UUID.randomUUID().toString().take(8))

            val notice = NoticeItem(
                id = noticeId,
                title = title,
                summary = summary,
                fullContent = fullContent,
                category = category,
                publishedDate = dateStr,
                isPinned = isPinned,
                author = author.ifBlank { "মসজিদ পরিচালনা কমিটি" }
            )

            adminRepo.saveNotice(notice, _currentUser.value)
            _actionMessage.value = if (id != null) "বিজ্ঞপ্তি সফলভাবে আপডেট করা হয়েছে" else "নতুন বিজ্ঞপ্তি সফলভাবে প্রকাশ করা হয়েছে"
        }
    }

    fun togglePin(notice: NoticeItem) {
        viewModelScope.launch {
            val updated = notice.copy(isPinned = !notice.isPinned)
            adminRepo.saveNotice(updated, _currentUser.value)
            _actionMessage.value = if (updated.isPinned) "বিজ্ঞপ্তি পিন করা হয়েছে" else "বিজ্ঞপ্তি আনপিন করা হয়েছে"
        }
    }

    fun deleteNotice(noticeId: String, title: String) {
        viewModelScope.launch {
            adminRepo.deleteNotice(noticeId, title, _currentUser.value)
            _actionMessage.value = "বিজ্ঞপ্তি মুছে ফেলা হয়েছে"
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}
