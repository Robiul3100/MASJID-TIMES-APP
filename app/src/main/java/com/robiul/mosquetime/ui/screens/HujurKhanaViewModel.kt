package com.robiul.mosquetime.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robiul.mosquetime.data.model.DayMealSchedule
import com.robiul.mosquetime.data.model.MealSchedule
import com.robiul.mosquetime.data.model.MealStatus
import com.robiul.mosquetime.data.model.MealType
import com.robiul.mosquetime.data.model.MealWeeklySummary
import com.robiul.mosquetime.data.repository.MealScheduleRepository
import com.robiul.mosquetime.data.repository.MockMealScheduleRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface HujurKhanaUiState {
    object Loading : HujurKhanaUiState
    data class Success(
        val todaySchedule: DayMealSchedule,
        val tomorrowSchedule: DayMealSchedule?,
        val allSchedules: List<DayMealSchedule>,
        val weeklySummary: MealWeeklySummary
    ) : HujurKhanaUiState
    data class Error(val message: String) : HujurKhanaUiState
    object Empty : HujurKhanaUiState
}

class HujurKhanaViewModel(
    private val repository: MealScheduleRepository = MockMealScheduleRepository
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isCalendarView = MutableStateFlow(false)
    val isCalendarView: StateFlow<Boolean> = _isCalendarView.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMealTypeFilter = MutableStateFlow<MealType?>(null)
    val selectedMealTypeFilter: StateFlow<MealType?> = _selectedMealTypeFilter.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow<MealStatus?>(null)
    val selectedStatusFilter: StateFlow<MealStatus?> = _selectedStatusFilter.asStateFlow()

    private val _selectedDateStr = MutableStateFlow(repository.getTodaySchedule().dateStr)
    val selectedDateStr: StateFlow<String> = _selectedDateStr.asStateFlow()

    private val _selectedMealDetail = MutableStateFlow<MealSchedule?>(null)
    val selectedMealDetail: StateFlow<MealSchedule?> = _selectedMealDetail.asStateFlow()

    private val _currentMealType = MutableStateFlow(repository.getCurrentMealType())
    val currentMealType: StateFlow<MealType> = _currentMealType.asStateFlow()

    val daySchedules = repository.daySchedules

    val uiState: StateFlow<HujurKhanaUiState> = combine(
        repository.daySchedules,
        _isRefreshing
    ) { schedules, refreshing ->
        if (refreshing && schedules.isEmpty()) {
            HujurKhanaUiState.Loading
        } else if (schedules.isEmpty()) {
            HujurKhanaUiState.Empty
        } else {
            val today = schedules.find { it.isToday } ?: schedules.first()
            val tomorrow = schedules.find { it.isTomorrow }
            val summary = repository.getWeeklySummary()
            HujurKhanaUiState.Success(
                todaySchedule = today,
                tomorrowSchedule = tomorrow,
                allSchedules = schedules,
                weeklySummary = summary
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HujurKhanaUiState.Loading
    )

    // Filtered schedules for search & tab filter
    val filteredSchedules: StateFlow<List<DayMealSchedule>> = combine(
        repository.daySchedules,
        _searchQuery,
        _selectedMealTypeFilter,
        _selectedStatusFilter
    ) { schedules, query, mealFilter, statusFilter ->
        if (query.isBlank() && mealFilter == null && statusFilter == null) {
            schedules
        } else {
            repository.searchSchedules(query, mealFilter, statusFilter)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectDate(dateStr: String) {
        _selectedDateStr.value = dateStr
    }

    fun toggleCalendarView() {
        _isCalendarView.value = !_isCalendarView.value
    }

    fun setCalendarView(enabled: Boolean) {
        _isCalendarView.value = enabled
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onMealTypeFilterSelect(type: MealType?) {
        _selectedMealTypeFilter.value = if (_selectedMealTypeFilter.value == type) null else type
    }

    fun onStatusFilterSelect(status: MealStatus?) {
        _selectedStatusFilter.value = if (_selectedStatusFilter.value == status) null else status
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedMealTypeFilter.value = null
        _selectedStatusFilter.value = null
    }

    fun showMealDetail(meal: MealSchedule) {
        _selectedMealDetail.value = meal
    }

    fun closeMealDetail() {
        _selectedMealDetail.value = null
    }

    fun updateMealStatus(scheduleId: String, newStatus: MealStatus) {
        repository.updateMealStatus(scheduleId, newStatus)
        // If current detail is open, update it
        _selectedMealDetail.value?.let { current ->
            if (current.id == scheduleId) {
                _selectedMealDetail.value = current.copy(status = newStatus)
            }
        }
    }

    fun toggleReminder(scheduleId: String) {
        repository.toggleReminder(scheduleId)
        _selectedMealDetail.value?.let { current ->
            if (current.id == scheduleId) {
                _selectedMealDetail.value = current.copy(isReminderActive = !current.isReminderActive)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _currentMealType.value = repository.getCurrentMealType()
            delay(400) // gentle feedback
            _isRefreshing.value = false
        }
    }
}
