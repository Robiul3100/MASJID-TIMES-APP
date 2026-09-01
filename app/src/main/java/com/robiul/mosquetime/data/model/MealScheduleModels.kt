package com.robiul.mosquetime.data.model

/**
 * Meal types for Imam's food schedule in Bangladeshi mosque system
 */
enum class MealType(
    val titleBn: String,
    val shortNameBn: String,
    val timeRangeBn: String,
    val descriptionBn: String
) {
    MORNING(
        titleBn = "সকালের খানা",
        shortNameBn = "সকাল",
        timeRangeBn = "সকাল ০৬:০০ – ১০:৩০",
        descriptionBn = "সকালের নাস্তা ও চা"
    ),
    LUNCH(
        titleBn = "দুপুরের খানা",
        shortNameBn = "দুপুর",
        timeRangeBn = "দুপুর ১০:৩১ – বিকাল ০৫:০০",
        descriptionBn = "দুপুরের প্রধান আহার"
    ),
    DINNER(
        titleBn = "রাতের খানা",
        shortNameBn = "রাত",
        timeRangeBn = "সন্ধ্যা ০৫:০১ – রাত ১১:৫৯",
        descriptionBn = "রাতের সান্ধ্যভোজ"
    )
}

/**
 * Delivery status for each meal
 */
enum class MealStatus(
    val titleBn: String,
    val badgeText: String,
    val iconName: String
) {
    DELIVERED(
        titleBn = "দেওয়া হয়েছে",
        badgeText = "✓ দেওয়া হয়েছে",
        iconName = "check_circle"
    ),
    PENDING(
        titleBn = "অপেক্ষমাণ",
        badgeText = "⏳ অপেক্ষমাণ",
        iconName = "hourglass_top"
    ),
    UPCOMING(
        titleBn = "আগামী",
        badgeText = "🕒 আগামী",
        iconName = "schedule"
    ),
    MISSED(
        titleBn = "দেওয়া হয়নি",
        badgeText = "⚠️ দেওয়া হয়নি",
        iconName = "warning"
    )
}

/**
 * Configurable meal time ranges
 */
data class MealPeriodConfig(
    val morningStartMinutes: Int = 6 * 60,         // 06:00
    val morningEndMinutes: Int = 10 * 60 + 30,     // 10:30
    val lunchStartMinutes: Int = 10 * 60 + 31,     // 10:31
    val lunchEndMinutes: Int = 17 * 60,            // 17:00
    val dinnerStartMinutes: Int = 17 * 60 + 1,     // 17:01
    val dinnerEndMinutes: Int = 23 * 60 + 59       // 23:59
) {
    fun getCurrentMealType(currentMinuteOfDay: Int): MealType {
        return when {
            currentMinuteOfDay in morningStartMinutes..morningEndMinutes -> MealType.MORNING
            currentMinuteOfDay in lunchStartMinutes..lunchEndMinutes -> MealType.LUNCH
            else -> MealType.DINNER
        }
    }
}

/**
 * Individual Meal Schedule item
 */
data class MealSchedule(
    val id: String,
    val date: String, // yyyy-MM-dd format (e.g. 2026-08-29)
    val mealType: MealType,
    val responsiblePersonName: String,
    val householdName: String,
    val status: MealStatus,
    val deliveredAt: String? = null, // e.g. "সকাল ০৮:১৫"
    val notes: String? = null, // Special menu item or notes
    val phoneNumber: String? = null,
    val area: String = "উত্তর পাড়া",
    val specialItems: List<String> = emptyList(),
    val isReminderActive: Boolean = false
)

/**
 * Daily schedule where ONE household is responsible for all 3 meals of that day
 */
data class DayMealSchedule(
    val dateStr: String, // yyyy-MM-dd
    val dateBn: String, // e.g. "২৯ আগস্ট ২০২৬"
    val dayNameBn: String, // e.g. "শুক্রবার"
    val relativeDayLabelBn: String, // "আজ", "কাল", "পরশু", or formatted day
    val hijriDateBn: String, // e.g. "১৫ সফর ১৪৪৮"
    val hostHouseholdName: String,
    val hostResponsiblePerson: String,
    val hostPhoneNumber: String? = null,
    val hostArea: String = "উত্তর পাড়া",
    val rotationTurnBn: String = "রোটেশন: ০১/১৫ (১ম বার)",
    val notes: String? = null,
    val morningMeal: MealSchedule,
    val lunchMeal: MealSchedule,
    val dinnerMeal: MealSchedule,
    val isToday: Boolean = false,
    val isTomorrow: Boolean = false,
    val isDayAfterTomorrow: Boolean = false
) {
    val allMeals: List<MealSchedule>
        get() = listOf(morningMeal, lunchMeal, dinnerMeal)

    val isAllDelivered: Boolean
        get() = allMeals.all { it.status == MealStatus.DELIVERED }

    val hasPending: Boolean
        get() = allMeals.any { it.status == MealStatus.PENDING }

    val hasMissed: Boolean
        get() = allMeals.any { it.status == MealStatus.MISSED }
}

/**
 * Weekly summary statistics for meal management
 */
data class MealWeeklySummary(
    val totalMeals: Int,
    val deliveredCount: Int,
    val pendingCount: Int,
    val missedCount: Int,
    val upcomingCount: Int,
    val totalHouseholdsCount: Int = 15,
    val daysInCycleCount: Int = 30
)

/**
 * Registered household in the mosque community roster
 */
data class Household(
    val id: String,
    val serialNumber: Int = 1, // e.g. 1 to 15
    val householdName: String,
    val responsiblePersonName: String,
    val area: String,
    val phoneNumber: String? = null,
    val notes: String? = null,
    val totalServedCount: Int = 0
)

