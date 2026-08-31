package com.example.feature.admin.committee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.AuthRepository
import com.example.data.firebase.MosqueAdminRepository
import com.example.data.model.AuditActionCategory
import com.example.data.model.CommitteeCategory
import com.example.data.model.CommitteeMember
import com.example.data.repository.MosqueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminCommitteeUiState(
    val members: List<CommitteeMember> = emptyList(),
    val filteredMembers: List<CommitteeMember> = emptyList(),
    val selectedCategory: CommitteeCategory? = null,
    val searchQuery: String = "",
    val isAddEditOpen: Boolean = false,
    val editingMember: CommitteeMember? = null,
    val isSubmitting: Boolean = false,
    val userMessage: String? = null
)

@HiltViewModel
class AdminCommitteeViewModel @Inject constructor(
    private val adminRepo: MosqueAdminRepository,
    private val authRepository: AuthRepository,
    private val mosqueRepository: MosqueRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<CommitteeCategory?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _isAddEditOpen = MutableStateFlow(false)
    private val _editingMember = MutableStateFlow<CommitteeMember?>(null)
    private val _isSubmitting = MutableStateFlow(false)
    private val _userMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AdminCommitteeUiState> = combine(
        mosqueRepository.committeeFlow,
        _selectedCategory,
        _searchQuery
    ) { members: List<CommitteeMember>, category: CommitteeCategory?, query: String ->
        val filtered = members.filter { member ->
            val matchesCategory = category == null || category == CommitteeCategory.ALL || member.category == category
            val matchesQuery = query.isBlank() ||
                    member.name.contains(query, ignoreCase = true) ||
                    member.designationBn.contains(query, ignoreCase = true) ||
                    member.phone.contains(query, ignoreCase = true) ||
                    member.profession.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }

        AdminCommitteeUiState(
            members = members,
            filteredMembers = filtered,
            selectedCategory = category,
            searchQuery = query,
            isAddEditOpen = _isAddEditOpen.value,
            editingMember = _editingMember.value,
            isSubmitting = _isSubmitting.value,
            userMessage = _userMessage.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AdminCommitteeUiState()
    )

    fun onCategorySelected(category: CommitteeCategory?) {
        _selectedCategory.value = category
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onAddClick() {
        _editingMember.value = null
        _isAddEditOpen.value = true
    }

    fun onEditClick(member: CommitteeMember) {
        _editingMember.value = member
        _isAddEditOpen.value = true
    }

    fun onDismissDialog() {
        _isAddEditOpen.value = false
        _editingMember.value = null
    }

    fun onSaveMember(member: CommitteeMember) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val admin = authRepository.currentUser.value
            val result = adminRepo.saveCommitteeMembers(
                mosqueRepository.committeeMembers.toMutableList().apply {
                    val idx = indexOfFirst { it.id == member.id }
                    if (idx >= 0) set(idx, member) else add(member)
                },
                admin
            )
            
            if (result.isSuccess) {
                mosqueRepository.addOrUpdateCommitteeMember(member)
                _userMessage.value = "কমিটি তথ্য সফলভাবে সংরক্ষিত হয়েছে"
                onDismissDialog()
            } else {
                _userMessage.value = "ত্রুটি: ${result.exceptionOrNull()?.message}"
            }
            _isSubmitting.value = false
        }
    }

    fun onDeleteMember(memberId: String) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val admin = authRepository.currentUser.value
            val result = adminRepo.saveCommitteeMembers(
                mosqueRepository.committeeMembers.filter { it.id != memberId },
                admin
            )

            if (result.isSuccess) {
                mosqueRepository.deleteCommitteeMember(memberId)
                _userMessage.value = "সদস্য তালিকা থেকে অপসারিত হয়েছে"
            } else {
                _userMessage.value = "মুছে ফেলা সম্ভব হয়নি"
            }
            _isSubmitting.value = false
        }
    }

    fun clearMessage() {
        _userMessage.value = null
    }
}
