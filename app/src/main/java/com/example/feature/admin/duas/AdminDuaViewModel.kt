package com.example.feature.admin.duas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.AuthRepository
import com.example.data.model.AuditActionCategory
import com.example.data.model.DuaCategory
import com.example.data.model.DuaItem
import com.example.data.repository.MosqueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AdminDuaUiState(
    val duas: List<DuaItem> = emptyList(),
    val filteredDuas: List<DuaItem> = emptyList(),
    val selectedCategory: DuaCategory? = null,
    val searchQuery: String = "",
    val isAddEditOpen: Boolean = false,
    val editingDua: DuaItem? = null,
    val isSubmitting: Boolean = false,
    val userMessage: String? = null
)

class AdminDuaViewModel : ViewModel() {

    private val _selectedCategory = MutableStateFlow<DuaCategory?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _isAddEditOpen = MutableStateFlow(false)
    private val _editingDua = MutableStateFlow<DuaItem?>(null)
    private val _isSubmitting = MutableStateFlow(false)
    private val _userMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AdminDuaUiState> = combine(
        MosqueRepository.duasFlow,
        _selectedCategory,
        _searchQuery
    ) { duas: List<DuaItem>, category: DuaCategory?, query: String ->
        val filtered = duas.filter { dua ->
            val matchesCategory = category == null || category == DuaCategory.ALL || dua.category == category
            val matchesQuery = query.isBlank() ||
                    dua.titleBn.contains(query, ignoreCase = true) ||
                    dua.pronunciationBn.contains(query, ignoreCase = true) ||
                    dua.meaningBn.contains(query, ignoreCase = true) ||
                    dua.reference.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }

        AdminDuaUiState(
            duas = duas,
            filteredDuas = filtered,
            selectedCategory = category,
            searchQuery = query,
            isAddEditOpen = _isAddEditOpen.value,
            editingDua = _editingDua.value,
            isSubmitting = _isSubmitting.value,
            userMessage = _userMessage.value
        )
    }.combine(_isAddEditOpen) { state, isOpen ->
        state.copy(isAddEditOpen = isOpen)
    }.combine(_editingDua) { state, editing ->
        state.copy(editingDua = editing)
    }.combine(_isSubmitting) { state, submitting ->
        state.copy(isSubmitting = submitting)
    }.combine(_userMessage) { state, message ->
        state.copy(userMessage = message)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AdminDuaUiState()
    )

    fun onCategorySelected(category: DuaCategory?) {
        _selectedCategory.value = category
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun openAddDialog() {
        _editingDua.value = null
        _isAddEditOpen.value = true
    }

    fun openEditDialog(dua: DuaItem) {
        _editingDua.value = dua
        _isAddEditOpen.value = true
    }

    fun closeDialog() {
        _isAddEditOpen.value = false
        _editingDua.value = null
    }

    fun saveDua(
        id: String,
        titleBn: String,
        category: DuaCategory,
        arabicText: String,
        pronunciationBn: String,
        meaningBn: String,
        reference: String,
        benefit: String,
        repetitionCount: Int
    ) {
        if (titleBn.isBlank() || arabicText.isBlank() || meaningBn.isBlank()) {
            _userMessage.value = "অনুগ্রহ করে শিরোনাম, আরবি ও অর্থ পূরণ করুন"
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            val duaId = id.ifBlank { "dua_${System.currentTimeMillis()}" }
            val newDua = DuaItem(
                id = duaId,
                titleBn = titleBn.trim(),
                category = category,
                arabicText = arabicText.trim(),
                pronunciationBn = pronunciationBn.trim(),
                meaningBn = meaningBn.trim(),
                reference = reference.trim(),
                benefit = benefit.trim(),
                repetitionCount = if (repetitionCount > 0) repetitionCount else 1
            )

            val isNew = id.isBlank()
            MosqueRepository.addOrUpdateDua(newDua)

            val admin = AuthRepository.getInstance().currentUser.value
            val adminName = admin?.nameBn ?: "অ্যাডমিন"
            val adminRole = admin?.role?.displayNameBn ?: "পরিচালক"

            MosqueRepository.logAdminAction(
                adminNameBn = adminName,
                adminRoleBn = adminRole,
                category = AuditActionCategory.DUAS,
                actionTitleBn = if (isNew) "নতুন দোয়া যুক্ত করা হয়েছে" else "দোয়ার তথ্য আপডেট করা হয়েছে",
                detailsBn = "${newDua.titleBn} (${newDua.category.titleBn})"
            )

            _isSubmitting.value = false
            _isAddEditOpen.value = false
            _editingDua.value = null
            _userMessage.value = if (isNew) "দোয়া সফলভাবে সংরক্ষণ হয়েছে" else "দোয়া সফলভাবে আপডেট হয়েছে"
        }
    }

    fun deleteDua(dua: DuaItem) {
        viewModelScope.launch {
            MosqueRepository.deleteDua(dua.id)

            val admin = AuthRepository.getInstance().currentUser.value
            val adminName = admin?.nameBn ?: "অ্যাডমিন"
            val adminRole = admin?.role?.displayNameBn ?: "পরিচালক"

            MosqueRepository.logAdminAction(
                adminNameBn = adminName,
                adminRoleBn = adminRole,
                category = AuditActionCategory.DUAS,
                actionTitleBn = "দোয়া মুছে ফেলা হয়েছে",
                detailsBn = "মুছে ফেলা দোয়া: ${dua.titleBn}"
            )

            _userMessage.value = "দোয়া মুছে ফেলা হয়েছে"
        }
    }

    fun clearMessage() {
        _userMessage.value = null
    }
}
