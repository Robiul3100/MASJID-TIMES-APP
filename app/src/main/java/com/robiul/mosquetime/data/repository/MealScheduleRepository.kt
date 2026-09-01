package com.robiul.mosquetime.data.repository

import com.robiul.mosquetime.data.model.DayMealSchedule
import com.robiul.mosquetime.data.model.Household
import com.robiul.mosquetime.data.model.MealPeriodConfig
import com.robiul.mosquetime.data.model.MealSchedule
import com.robiul.mosquetime.data.model.MealStatus
import com.robiul.mosquetime.data.model.MealType
import com.robiul.mosquetime.data.model.MealWeeklySummary
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for Imam's meal schedule (হুজুরের খানা).
 * Designed for clean migration to Firebase Firestore / Backend API.
 */
interface MealScheduleRepository {
    val daySchedules: StateFlow<List<DayMealSchedule>>
    val registeredHouseholds: StateFlow<List<Household>>
    val periodConfig: StateFlow<MealPeriodConfig>

    fun getTodaySchedule(): DayMealSchedule
    fun getScheduleForDate(dateStr: String): DayMealSchedule?
    fun getWeeklySummary(): MealWeeklySummary
    fun getCurrentMealType(): MealType
    fun getTomorrowSchedule(): DayMealSchedule?
    
    // Meal & Daily Host Operations
    fun updateMealStatus(scheduleId: String, newStatus: MealStatus, deliveredAt: String? = null)
    fun updateMealDetails(scheduleId: String, updatedMeal: MealSchedule)
    fun updateDayHost(
        dateStr: String,
        householdName: String,
        responsiblePerson: String,
        area: String,
        phone: String?,
        notes: String? = null
    )
    fun addOrUpdateDaySchedule(daySchedule: DayMealSchedule)
    fun deleteDaySchedule(dateStr: String)
    
    // Household Roster CRUD
    fun addHousehold(household: Household)
    fun updateHousehold(household: Household)
    fun deleteHousehold(householdId: String)
    fun generateMonthlyRotation(startDateStr: String? = null)

    fun toggleReminder(scheduleId: String)
    fun updatePeriodConfig(config: MealPeriodConfig)
    
    fun searchSchedules(
        query: String,
        filterMealType: MealType?,
        filterStatus: MealStatus?
    ): List<DayMealSchedule>
}

