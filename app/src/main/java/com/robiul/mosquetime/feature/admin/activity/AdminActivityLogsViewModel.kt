package com.robiul.mosquetime.feature.admin.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robiul.mosquetime.data.model.AdminAuditLog
import com.robiul.mosquetime.data.model.AuditActionCategory
import com.robiul.mosquetime.data.repository.MosqueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AdminActivityLogsUiState(
    val logs: List<AdminAuditLog> = emptyList(),
    val filteredLogs: List<AdminAuditLog> = emptyList(),
    val selectedCategory: AuditActionCategory? = null,
    val searchQuery: String = "",
    val userMessage: String? = null
)

class AdminActivityLogsViewModel : ViewModel() {

    private val _selectedCategory = MutableStateFlow<AuditActionCategory?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _userMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AdminActivityLogsUiState> = combine(
        MosqueRepository.auditLogsFlow,
        _selectedCategory,
        _searchQuery,
        _userMessage
    ) { logs, category, query, message ->
        val filtered = logs.filter { log ->
            val matchesCategory = category == null || category == AuditActionCategory.ALL || log.category == category
            val matchesQuery = query.isBlank() ||
                    log.actionTitleBn.contains(query, ignoreCase = true) ||
                    log.adminNameBn.contains(query, ignoreCase = true) ||
                    log.detailsBn.contains(query, ignoreCase = true) ||
                    log.adminRoleBn.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }

        AdminActivityLogsUiState(
            logs = logs,
            filteredLogs = filtered,
            selectedCategory = category,
            searchQuery = query,
            userMessage = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AdminActivityLogsUiState()
    )

    fun onCategorySelected(category: AuditActionCategory?) {
        _selectedCategory.value = category
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            MosqueRepository.clearAuditLogs()
            _userMessage.value = "কার্যক্রম লগ সম্পূর্ণ পরিষ্কার করা হয়েছে"
        }
    }

    fun clearMessage() {
        _userMessage.value = null
    }
}
