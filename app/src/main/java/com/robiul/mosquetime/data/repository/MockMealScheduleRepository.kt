package com.robiul.mosquetime.data.repository

import com.robiul.mosquetime.data.model.DayMealSchedule
import com.robiul.mosquetime.data.model.Household
import com.robiul.mosquetime.data.model.MealPeriodConfig
import com.robiul.mosquetime.data.model.MealSchedule
import com.robiul.mosquetime.data.model.MealStatus
import com.robiul.mosquetime.data.model.MealType
import com.robiul.mosquetime.data.model.MealWeeklySummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Mock repository providing authentic Bangladeshi mosque community data for Imam's food schedule.
 * Reactively emits updates via StateFlow and supports all interactions.
 */
object MockMealScheduleRepository : MealScheduleRepository {

    private val _periodConfig = MutableStateFlow(MealPeriodConfig())
    override val periodConfig: StateFlow<MealPeriodConfig> = _periodConfig.asStateFlow()

    private val _registeredHouseholds = MutableStateFlow<List<Household>>(createInitialHouseholds())
    override val registeredHouseholds: StateFlow<List<Household>> = _registeredHouseholds.asStateFlow()

    private val _daySchedules = MutableStateFlow<List<DayMealSchedule>>(createInitialSchedules())
    override val daySchedules: StateFlow<List<DayMealSchedule>> = _daySchedules.asStateFlow()

    private fun createInitialHouseholds(): List<Household> {
        return listOf(
            Household("h1", "আব্দুল করিমের বাড়ি (হাজী বাড়ি)", "মোঃ আব্দুল করিম", "উত্তর পাড়া", "01711-234567", 12),
            Household("h2", "মোঃ রফিকুল ইসলামের বাড়ি", "মোঃ রফিকুল ইসলাম", "দক্ষিণ পাড়া", "01819-876543", 10),
            Household("h3", "মোঃ জসিম উদ্দিনের বাড়ি", "মোঃ জসিম উদ্দিন", "পশ্চিম পাড়া", "01912-345678", 14),
            Household("h4", "মোঃ হাবিবুর রহমানের বাড়ি (সরকার বাড়ি)", "মোঃ হাবিবুর রহমান সরকার", "পূর্ব পাড়া", "01723-456789", 8),
            Household("h5", "মোঃ কামালের বাড়ি (মুন্সি বাড়ি)", "মোঃ কামাল হোসেন মুন্সি", "মধ্য পাড়া", "01611-987654", 15),
            Household("h6", "মোঃ সালামের বাড়ি (কাজী বাড়ি)", "কাজী মোঃ আব্দুস সালাম", "হাজী পাড়া", "01734-567890", 11),
            Household("h7", "মোঃ লিয়াকত আলীর বাড়ি (মাতব্বর বাড়ি)", "মোঃ লিয়াকত আলী মাতব্বর", "উত্তর পাড়া", "01822-334455", 9),
            Household("h8", "হাজী মোঃ নুরুল হকের বাড়ি", "হাজী মোঃ নুরুল হক", "দক্ষিণ পাড়া", "01933-445566", 16),
            Household("h9", "মোঃ আনোয়ার হোসেনের বাড়ি (চৌধুরী বাড়ি)", "মোঃ আনোয়ার চৌধুরী", "পূর্ব পাড়া", "01744-556677", 13),
            Household("h10", "মোঃ মোস্তফা কামালের বাড়ি (শিকদার বাড়ি)", "মোঃ মোস্তফা কামাল শিকদার", "পশ্চিম পাড়া", "01855-667788", 7),
            Household("h11", "মোঃ আলতাফ হোসেনের বাড়ি (তালুকদার বাড়ি)", "মোঃ আলতাফ তালুকদার", "মধ্য পাড়া", "01966-778899", 11),
            Household("h12", "মোঃ খলিলুর রহমানের বাড়ি (প্রামাণিক বাড়ি)", "মোঃ খলিলুর রহমান", "উত্তর পাড়া", "01777-889900", 10)
        )
    }

    private fun createInitialSchedules(): List<DayMealSchedule> {
        val list = mutableListOf<DayMealSchedule>()
        val calendar = Calendar.getInstance()

        // Set base calendar to 2026-08-29 (matching app's current anchor date or relative to current date)
        // If current year is 2026, we align with August 29, 2026
        calendar.set(2026, Calendar.AUGUST, 29, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Generate 3 days before today, today, and 20 days forward
        val households = createInitialHouseholds()
        var hIndex = 0

        for (offset in -3..21) {
            val dayCal = Calendar.getInstance().apply {
                time = calendar.time
                add(Calendar.DAY_OF_YEAR, offset)
            }

            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(dayCal.time)
            val dayOfMonth = dayCal.get(Calendar.DAY_OF_MONTH)
            val month = dayCal.get(Calendar.MONTH)
            val year = dayCal.get(Calendar.YEAR)
            val dayOfWeek = dayCal.get(Calendar.DAY_OF_WEEK)

            val monthNamesBn = listOf(
                "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
                "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
            )
            val daysOfWeekBn = listOf(
                "রবিবার", "সোমবার", "মঙ্গলবার", "বুধবার", "বৃহস্পতিবার", "শুক্রবার", "শনিবার"
            )

            val dateBn = "${toBanglaDigit(dayOfMonth)} ${monthNamesBn[month]} ${toBanglaDigit(year)}"
            val dayNameBn = daysOfWeekBn[dayOfWeek - 1]

            val relativeLabel = when (offset) {
                0 -> "আজ"
                1 -> "কাল"
                2 -> "পরশু"
                -1 -> "গতকাল"
                else -> "${toBanglaDigit(dayOfMonth)} ${monthNamesBn[month].take(3)}"
            }

            val hijriDay = ((15 + offset - 1) % 30) + 1
            val hijriDateBn = "${toBanglaDigit(hijriDay)} সফর ১৪৪৮"

            val h1 = households[hIndex % households.size]
            val h2 = households[(hIndex + 1) % households.size]
            val h3 = households[(hIndex + 2) % households.size]
            hIndex = (hIndex + 3) % households.size

            // Status determination
            val (morningStatus, morningDeliveredAt) = when {
                offset < 0 -> Pair(MealStatus.DELIVERED, "সকাল ০৮:১৫")
                offset == 0 -> Pair(MealStatus.DELIVERED, "সকাল ০৮:২০")
                else -> Pair(MealStatus.UPCOMING, null)
            }

            val (lunchStatus, lunchDeliveredAt) = when {
                offset < 0 -> Pair(MealStatus.DELIVERED, "দুপুর ০১:৩০")
                offset == 0 -> Pair(MealStatus.PENDING, null)
                else -> Pair(MealStatus.UPCOMING, null)
            }

            val (dinnerStatus, dinnerDeliveredAt) = when {
                offset < 0 -> Pair(MealStatus.DELIVERED, "রাত ০৯:০০")
                offset == 0 -> Pair(MealStatus.UPCOMING, null)
                else -> Pair(MealStatus.UPCOMING, null)
            }

            val morningMeal = MealSchedule(
                id = "${dateStr}_morning",
                date = dateStr,
                mealType = MealType.MORNING,
                responsiblePersonName = h1.responsiblePersonName,
                householdName = h1.householdName,
                status = morningStatus,
                deliveredAt = morningDeliveredAt,
                notes = if (offset == 0) "পরোটা, ডিম ভাজি ও দেশি গরুর খাঁটি দুধের চা" else "সকালের নাস্তা ও চা",
                phoneNumber = h1.phoneNumber,
                area = h1.area,
                specialItems = listOf("পরোটা", "ডিম ভাজি", "চা"),
                isReminderActive = offset == 0
            )

            val lunchMeal = MealSchedule(
                id = "${dateStr}_lunch",
                date = dateStr,
                mealType = MealType.LUNCH,
                responsiblePersonName = h2.responsiblePersonName,
                householdName = h2.householdName,
                status = lunchStatus,
                deliveredAt = lunchDeliveredAt,
                notes = if (offset == 0) "কাচ্চি বিরিয়ানি, দেশি মুরগির রোস্ট ও ফিরনি" else "দুপুরের প্রধান খাবার",
                phoneNumber = h2.phoneNumber,
                area = h2.area,
                specialItems = listOf("পোলাও/বিরিয়ানি", "দেশি মুরগি", "সালাদ", "মিষ্টি"),
                isReminderActive = offset in 0..1
            )

            val dinnerMeal = MealSchedule(
                id = "${dateStr}_dinner",
                date = dateStr,
                mealType = MealType.DINNER,
                responsiblePersonName = h3.responsiblePersonName,
                householdName = h3.householdName,
                status = dinnerStatus,
                deliveredAt = dinnerDeliveredAt,
                notes = if (offset == 0) "সাদা ভাত, রুই মাছের ঝোল ও মসুর ডাল" else "রাতের হালকা আহার",
                phoneNumber = h3.phoneNumber,
                area = h3.area,
                specialItems = listOf("সাদা ভাত", "মাছের ঝোল", "ডাল"),
                isReminderActive = false
            )

            list.add(
                DayMealSchedule(
                    dateStr = dateStr,
                    dateBn = dateBn,
                    dayNameBn = dayNameBn,
                    relativeDayLabelBn = relativeLabel,
                    hijriDateBn = hijriDateBn,
                    morningMeal = morningMeal,
                    lunchMeal = lunchMeal,
                    dinnerMeal = dinnerMeal,
                    isToday = offset == 0,
                    isTomorrow = offset == 1,
                    isDayAfterTomorrow = offset == 2
                )
            )
        }

        return list
    }

    override fun getTodaySchedule(): DayMealSchedule {
        return _daySchedules.value.find { it.isToday }
            ?: _daySchedules.value.firstOrNull()
            ?: createInitialSchedules().first()
    }

    override fun getTomorrowSchedule(): DayMealSchedule? {
        return _daySchedules.value.find { it.isTomorrow }
    }

    override fun getScheduleForDate(dateStr: String): DayMealSchedule? {
        return _daySchedules.value.find { it.dateStr == dateStr }
    }

    override fun getWeeklySummary(): MealWeeklySummary {
        val currentList = _daySchedules.value
        // Take 7 days around today (-1 to +5)
        val weekList = currentList.filter { 
            it.isToday || it.isTomorrow || it.isDayAfterTomorrow || 
            it.relativeDayLabelBn == "গতকাল" || it.relativeDayLabelBn.contains("১") || 
            it.relativeDayLabelBn.contains("২") || it.relativeDayLabelBn.contains("৩")
        }.take(7)

        val allMeals = weekList.flatMap { it.allMeals }
        val delivered = allMeals.count { it.status == MealStatus.DELIVERED }
        val pending = allMeals.count { it.status == MealStatus.PENDING }
        val missed = allMeals.count { it.status == MealStatus.MISSED }
        val upcoming = allMeals.count { it.status == MealStatus.UPCOMING }

        return MealWeeklySummary(
            totalMeals = allMeals.size,
            deliveredCount = delivered,
            pendingCount = pending,
            missedCount = missed,
            upcomingCount = upcoming
        )
    }

    override fun getCurrentMealType(): MealType {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentMinuteOfDay = currentHour * 60 + currentMinute
        return _periodConfig.value.getCurrentMealType(currentMinuteOfDay)
    }

    override fun updateMealStatus(scheduleId: String, newStatus: MealStatus, deliveredAt: String?) {
        val timeStamp = deliveredAt ?: if (newStatus == MealStatus.DELIVERED) {
            val cal = Calendar.getInstance()
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val min = cal.get(Calendar.MINUTE)
            val period = if (hour < 12) "সকাল" else if (hour < 17) "দুপুর" else "রাত"
            val displayHour = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
            "$period ${toBanglaDigit(displayHour)}:${toBanglaDigit(String.format("%02d", min))}"
        } else null

        val updated = _daySchedules.value.map { day ->
            val m = if (day.morningMeal.id == scheduleId) day.morningMeal.copy(status = newStatus, deliveredAt = timeStamp) else day.morningMeal
            val l = if (day.lunchMeal.id == scheduleId) day.lunchMeal.copy(status = newStatus, deliveredAt = timeStamp) else day.lunchMeal
            val d = if (day.dinnerMeal.id == scheduleId) day.dinnerMeal.copy(status = newStatus, deliveredAt = timeStamp) else day.dinnerMeal
            day.copy(morningMeal = m, lunchMeal = l, dinnerMeal = d)
        }
        _daySchedules.value = updated
    }

    override fun updateMealDetails(scheduleId: String, updatedMeal: MealSchedule) {
        val updated = _daySchedules.value.map { day ->
            val m = if (day.morningMeal.id == scheduleId) updatedMeal else day.morningMeal
            val l = if (day.lunchMeal.id == scheduleId) updatedMeal else day.lunchMeal
            val d = if (day.dinnerMeal.id == scheduleId) updatedMeal else day.dinnerMeal
            day.copy(morningMeal = m, lunchMeal = l, dinnerMeal = d)
        }
        _daySchedules.value = updated
    }

    override fun addHousehold(household: Household) {
        val current = _registeredHouseholds.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.id == household.id }
        if (existingIndex >= 0) {
            current[existingIndex] = household
        } else {
            current.add(household)
        }
        _registeredHouseholds.value = current
    }

    override fun toggleReminder(scheduleId: String) {
        val updated = _daySchedules.value.map { day ->
            val m = if (day.morningMeal.id == scheduleId) day.morningMeal.copy(isReminderActive = !day.morningMeal.isReminderActive) else day.morningMeal
            val l = if (day.lunchMeal.id == scheduleId) day.lunchMeal.copy(isReminderActive = !day.lunchMeal.isReminderActive) else day.lunchMeal
            val d = if (day.dinnerMeal.id == scheduleId) day.dinnerMeal.copy(isReminderActive = !day.dinnerMeal.isReminderActive) else day.dinnerMeal
            day.copy(morningMeal = m, lunchMeal = l, dinnerMeal = d)
        }
        _daySchedules.value = updated
    }

    override fun updatePeriodConfig(config: MealPeriodConfig) {
        _periodConfig.value = config
    }

    override fun searchSchedules(
        query: String,
        filterMealType: MealType?,
        filterStatus: MealStatus?
    ): List<DayMealSchedule> {
        val list = _daySchedules.value
        val cleanQuery = query.trim().lowercase(Locale.ROOT)

        return list.filter { day ->
            val matchesQuery = cleanQuery.isEmpty() ||
                    day.dateBn.lowercase().contains(cleanQuery) ||
                    day.dayNameBn.lowercase().contains(cleanQuery) ||
                    day.relativeDayLabelBn.lowercase().contains(cleanQuery) ||
                    day.allMeals.any { meal ->
                        meal.responsiblePersonName.lowercase().contains(cleanQuery) ||
                        meal.householdName.lowercase().contains(cleanQuery) ||
                        meal.area.lowercase().contains(cleanQuery)
                    }

            val matchesMealType = filterMealType == null || when (filterMealType) {
                MealType.MORNING -> day.morningMeal.status == (filterStatus ?: day.morningMeal.status)
                MealType.LUNCH -> day.lunchMeal.status == (filterStatus ?: day.lunchMeal.status)
                MealType.DINNER -> day.dinnerMeal.status == (filterStatus ?: day.dinnerMeal.status)
            }

            val matchesStatus = filterStatus == null || day.allMeals.any { it.status == filterStatus }

            matchesQuery && (filterMealType == null || matchesMealType) && matchesStatus
        }
    }

    private fun toBanglaDigit(number: Any): String {
        val banglaDigits = mapOf(
            '0' to '০', '1' to '১', '2' to '২', '3' to '৩', '4' to '৪',
            '5' to '৫', '6' to '৬', '7' to '৭', '8' to '৮', '9' to '৯'
        )
        return number.toString().map { banglaDigits[it] ?: it }.joinToString("")
    }
}
