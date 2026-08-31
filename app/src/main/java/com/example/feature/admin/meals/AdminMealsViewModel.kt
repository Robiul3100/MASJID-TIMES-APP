package com.example.feature.admin.meals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.auth.AdminUser
import com.example.core.auth.PermissionManager
import com.example.data.model.DayMealSchedule
import com.example.data.model.Household
import com.example.data.model.MealSchedule
import com.example.data.model.MealStatus
import com.example.data.model.MealType
import com.example.data.model.MealWeeklySummary
import com.example.data.repository.MealScheduleRepository
import com.example.data.repository.MockMealScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface AdminMealsUiState {
    object Loading : AdminMealsUiState
    data class Success(
        val allSchedules: List<DayMealSchedule>,
        val filteredSchedules: List<DayMealSchedule>,
        val households: List<Household>,
        val summary: MealWeeklySummary,
        val canEdit: Boolean
    ) : AdminMealsUiState
    data class Error(val message: String) : AdminMealsUiState
}

class AdminMealsViewModel(
    private val repository: MealScheduleRepository = MockMealScheduleRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow<MealStatus?>(null)
    val selectedStatusFilter: StateFlow<MealStatus?> = _selectedStatusFilter.asStateFlow()

    private val _selectedMealTypeFilter = MutableStateFlow<MealType?>(null)
    val selectedMealTypeFilter: StateFlow<MealType?> = _selectedMealTypeFilter.asStateFlow()

    private val _currentUser = MutableStateFlow<AdminUser?>(null)
    val currentUser: StateFlow<AdminUser?> = _currentUser.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    private val filterCriteria = combine(
        _searchQuery,
        _selectedStatusFilter,
        _selectedMealTypeFilter
    ) { query, status, type ->
        Triple(query, status, type)
    }

    val uiState: StateFlow<AdminMealsUiState> = combine(
        repository.daySchedules,
        repository.registeredHouseholds,
        filterCriteria,
        _currentUser
    ) { schedules, households, (query, statusFilter, mealFilter), user ->
        val canEdit = user == null || PermissionManager.canManageMealSchedules(user)
        val filtered = if (query.isBlank() && statusFilter == null && mealFilter == null) {
            schedules
        } else {
            repository.searchSchedules(query, mealFilter, statusFilter)
        }
        val summary = repository.getWeeklySummary()

        AdminMealsUiState.Success(
            allSchedules = schedules,
            filteredSchedules = filtered,
            households = households,
            summary = summary,
            canEdit = canEdit
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AdminMealsUiState.Loading
    )

    fun setCurrentAdmin(user: AdminUser?) {
        _currentUser.value = user
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onStatusFilterChange(status: MealStatus?) {
        _selectedStatusFilter.value = status
    }

    fun onMealTypeFilterChange(type: MealType?) {
        _selectedMealTypeFilter.value = type
    }

    fun updateMealStatus(scheduleId: String, newStatus: MealStatus) {
        viewModelScope.launch {
            repository.updateMealStatus(scheduleId, newStatus)
            _actionMessage.value = "খানার স্ট্যাটাস সফলভাবে আপডেট করা হয়েছে"
        }
    }

    fun updateMealDetails(
        mealId: String,
        responsiblePerson: String,
        householdName: String,
        area: String,
        phone: String,
        menuItems: List<String>,
        notes: String,
        existingMeal: MealSchedule
    ) {
        viewModelScope.launch {
            val updated = existingMeal.copy(
                responsiblePersonName = responsiblePerson,
                householdName = householdName,
                area = area,
                phoneNumber = phone,
                specialItems = menuItems,
                notes = notes
            )
            repository.updateMealDetails(mealId, updated)
            _actionMessage.value = "খানার দায়িত্ব পরিবর্তন ও সংরক্ষণ করা হয়েছে"
        }
    }

    fun addHousehold(name: String, responsible: String, area: String, phone: String) {
        viewModelScope.launch {
            val newHousehold = Household(
                id = "h_" + UUID.randomUUID().toString().take(6),
                householdName = name,
                responsiblePersonName = responsible,
                area = area,
                phoneNumber = phone,
                totalServedCount = 0
            )
            repository.addHousehold(newHousehold)
            _actionMessage.value = "নতুন পরিবার ডাটাবেজে যুক্ত হয়েছে"
        }
    }

    fun toggleReminder(scheduleId: String) {
        viewModelScope.launch {
            repository.toggleReminder(scheduleId)
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}
