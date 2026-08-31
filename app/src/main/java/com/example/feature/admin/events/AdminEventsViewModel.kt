package com.example.feature.admin.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.AuthRepository
import com.example.data.model.AuditActionCategory
import com.example.data.model.EventCategory
import com.example.data.model.MosqueEvent
import com.example.data.repository.MosqueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AdminEventsUiState(
    val events: List<MosqueEvent> = emptyList(),
    val filteredEvents: List<MosqueEvent> = emptyList(),
    val selectedCategory: EventCategory? = null,
    val searchQuery: String = "",
    val isAddEditOpen: Boolean = false,
    val editingEvent: MosqueEvent? = null,
    val isSubmitting: Boolean = false,
    val userMessage: String? = null
)

class AdminEventsViewModel : ViewModel() {

    private val _selectedCategory = MutableStateFlow<EventCategory?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _isAddEditOpen = MutableStateFlow(false)
    private val _editingEvent = MutableStateFlow<MosqueEvent?>(null)
    private val _isSubmitting = MutableStateFlow(false)
    private val _userMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AdminEventsUiState> = combine(
        MosqueRepository.eventsFlow,
        _selectedCategory,
        _searchQuery
    ) { events: List<MosqueEvent>, category: EventCategory?, query: String ->
        val filtered = events.filter { event ->
            val matchesCategory = category == null || category == EventCategory.ALL || event.category == category
            val matchesQuery = query.isBlank() ||
                    event.title.contains(query, ignoreCase = true) ||
                    event.speaker.contains(query, ignoreCase = true) ||
                    event.description.contains(query, ignoreCase = true) ||
                    event.locationBn.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }

        AdminEventsUiState(
            events = events,
            filteredEvents = filtered,
            selectedCategory = category,
            searchQuery = query,
            isAddEditOpen = _isAddEditOpen.value,
            editingEvent = _editingEvent.value,
            isSubmitting = _isSubmitting.value,
            userMessage = _userMessage.value
        )
    }.combine(_isAddEditOpen) { state, isOpen ->
        state.copy(isAddEditOpen = isOpen)
    }.combine(_editingEvent) { state, editing ->
        state.copy(editingEvent = editing)
    }.combine(_isSubmitting) { state, submitting ->
        state.copy(isSubmitting = submitting)
    }.combine(_userMessage) { state, message ->
        state.copy(userMessage = message)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AdminEventsUiState()
    )

    fun onCategorySelected(category: EventCategory?) {
        _selectedCategory.value = category
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun openAddDialog() {
        _editingEvent.value = null
        _isAddEditOpen.value = true
    }

    fun openEditDialog(event: MosqueEvent) {
        _editingEvent.value = event
        _isAddEditOpen.value = true
    }

    fun closeDialog() {
        _isAddEditOpen.value = false
        _editingEvent.value = null
    }

    fun saveEvent(
        id: String,
        title: String,
        dateBn: String,
        timeBn: String,
        locationBn: String,
        description: String,
        category: EventCategory,
        speaker: String,
        isUpcoming: Boolean
    ) {
        if (title.isBlank() || dateBn.isBlank()) {
            _userMessage.value = "অনুগ্রহ করে শিরোনাম ও তারিখ পূরণ করুন"
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            val eventId = id.ifBlank { "ev_${System.currentTimeMillis()}" }
            val newEvent = MosqueEvent(
                id = eventId,
                title = title.trim(),
                dateBn = dateBn.trim(),
                timeBn = timeBn.trim(),
                locationBn = locationBn.ifBlank { "বায়তুল আমান জামে মসজিদ" }.trim(),
                description = description.trim(),
                category = category,
                speaker = speaker.trim(),
                isUpcoming = isUpcoming
            )

            val isNew = id.isBlank()
            MosqueRepository.addOrUpdateEvent(newEvent)

            val admin = AuthRepository.getInstance().currentUser.value
            val adminName = admin?.nameBn ?: "অ্যাডমিন"
            val adminRole = admin?.role?.displayNameBn ?: "পরিচালক"

            MosqueRepository.logAdminAction(
                adminNameBn = adminName,
                adminRoleBn = adminRole,
                category = AuditActionCategory.EVENTS,
                actionTitleBn = if (isNew) "নতুন অনুষ্ঠান যোগ করা হয়েছে" else "অনুষ্ঠানের তথ্য আপডেট করা হয়েছে",
                detailsBn = "শিরোনাম: ${newEvent.title} (${newEvent.category.titleBn})"
            )

            _isSubmitting.value = false
            _isAddEditOpen.value = false
            _editingEvent.value = null
            _userMessage.value = if (isNew) "অনুষ্ঠান সফলভাবে যুক্ত হয়েছে" else "অনুষ্ঠান সফলভাবে আপডেট হয়েছে"
        }
    }

    fun deleteEvent(event: MosqueEvent) {
        viewModelScope.launch {
            MosqueRepository.deleteEvent(event.id)

            val admin = AuthRepository.getInstance().currentUser.value
            val adminName = admin?.nameBn ?: "অ্যাডমিন"
            val adminRole = admin?.role?.displayNameBn ?: "পরিচালক"

            MosqueRepository.logAdminAction(
                adminNameBn = adminName,
                adminRoleBn = adminRole,
                category = AuditActionCategory.EVENTS,
                actionTitleBn = "অনুষ্ঠান মুছে ফেলা হয়েছে",
                detailsBn = "মুছে ফেলা অনুষ্ঠান: ${event.title}"
            )

            _userMessage.value = "অনুষ্ঠান মুছে ফেলা হয়েছে"
        }
    }

    fun toggleUpcoming(event: MosqueEvent) {
        viewModelScope.launch {
            val updated = event.copy(isUpcoming = !event.isUpcoming)
            MosqueRepository.addOrUpdateEvent(updated)
            _userMessage.value = if (updated.isUpcoming) "অনুষ্ঠানটি চলমান হিসেবে চিহ্নিত করা হয়েছে" else "অনুষ্ঠানটি সম্পন্ন হিসেবে চিহ্নিত করা হয়েছে"
        }
    }

    fun clearMessage() {
        _userMessage.value = null
    }
}
