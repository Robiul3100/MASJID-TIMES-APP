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
    
    fun updateMealStatus(scheduleId: String, newStatus: MealStatus, deliveredAt: String? = null)
    fun updateMealDetails(scheduleId: String, updatedMeal: MealSchedule)
    fun addHousehold(household: Household)
    fun toggleReminder(scheduleId: String)
    fun updatePeriodConfig(config: MealPeriodConfig)
    
    fun searchSchedules(
        query: String,
        filterMealType: MealType?,
        filterStatus: MealStatus?
    ): List<DayMealSchedule>
}
