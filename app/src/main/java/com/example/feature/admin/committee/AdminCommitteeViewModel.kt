package com.example.feature.admin.committee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.AuthRepository
import com.example.data.model.AuditActionCategory
import com.example.data.model.CommitteeCategory
import com.example.data.model.CommitteeMember
import com.example.data.repository.MosqueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

class AdminCommitteeViewModel : ViewModel() {

    private val _selectedCategory = MutableStateFlow<CommitteeCategory?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _isAddEditOpen = MutableStateFlow(false)
    private val _editingMember = MutableStateFlow<CommitteeMember?>(null)
    private val _isSubmitting = MutableStateFlow(false)
    private val _userMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AdminCommitteeUiState> = combine(
        MosqueRepository.committeeFlow,
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
    }.combine(_isAddEditOpen) { state, isOpen ->
        state.copy(isAddEditOpen = isOpen)
    }.combine(_editingMember) { state, editing ->
        state.copy(editingMember = editing)
    }.combine(_isSubmitting) { state, submitting ->
        state.copy(isSubmitting = submitting)
    }.combine(_userMessage) { state, message ->
        state.copy(userMessage = message)
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

    fun openAddDialog() {
        _editingMember.value = null
        _isAddEditOpen.value = true
    }

    fun openEditDialog(member: CommitteeMember) {
        _editingMember.value = member
        _isAddEditOpen.value = true
    }

    fun closeDialog() {
        _isAddEditOpen.value = false
        _editingMember.value = null
    }

    fun saveMember(
        id: String,
        name: String,
        designationBn: String,
        category: CommitteeCategory,
        phone: String,
        profession: String,
        termYears: String
    ) {
        if (name.isBlank() || designationBn.isBlank()) {
            _userMessage.value = "অনুগ্রহ করে সদস্যের নাম ও পদবী লিখুন"
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            val memberId = id.ifBlank { "m_${System.currentTimeMillis()}" }
            val newMember = CommitteeMember(
                id = memberId,
                name = name.trim(),
                designationBn = designationBn.trim(),
                category = category,
                phone = phone.trim(),
                profession = profession.trim(),
                termYears = termYears.ifBlank { "২০২৪-২০২৬" }.trim()
            )

            val isNew = id.isBlank()
            MosqueRepository.addOrUpdateCommitteeMember(newMember)

            val admin = AuthRepository.getInstance().currentUser.value
            val adminName = admin?.nameBn ?: "অ্যাডমিন"
            val adminRole = admin?.role?.displayNameBn ?: "পরিচালক"

            MosqueRepository.logAdminAction(
                adminNameBn = adminName,
                adminRoleBn = adminRole,
                category = AuditActionCategory.COMMITTEE,
                actionTitleBn = if (isNew) "কমিটিতে নতুন সদস্য যুক্ত হয়েছে" else "সদস্যের তথ্য আপডেট করা হয়েছে",
                detailsBn = "${newMember.name} - ${newMember.designationBn} (${newMember.category.titleBn})"
            )

            _isSubmitting.value = false
            _isAddEditOpen.value = false
            _editingMember.value = null
            _userMessage.value = if (isNew) "সদস্য সফলভাবে যুক্ত করা হয়েছে" else "সদস্যের তথ্য আপডেট হয়েছে"
        }
    }

    fun deleteMember(member: CommitteeMember) {
        viewModelScope.launch {
            MosqueRepository.deleteCommitteeMember(member.id)

            val admin = AuthRepository.getInstance().currentUser.value
            val adminName = admin?.nameBn ?: "অ্যাডমিন"
            val adminRole = admin?.role?.displayNameBn ?: "পরিচালক"

            MosqueRepository.logAdminAction(
                adminNameBn = adminName,
                adminRoleBn = adminRole,
                category = AuditActionCategory.COMMITTEE,
                actionTitleBn = "কমিটি থেকে সদস্য অপসারণ",
                detailsBn = "অপসারিত: ${member.name} (${member.designationBn})"
            )

            _userMessage.value = "সদস্য অপসারণ করা হয়েছে"
        }
    }

    fun clearMessage() {
        _userMessage.value = null
    }
}
