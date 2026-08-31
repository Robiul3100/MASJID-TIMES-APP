package com.example.data.repository

import com.example.data.local.dao.OfflinePrayerScheduleDao
import com.example.data.local.dao.PrayerScheduleSettingsDao
import com.example.data.local.dao.UserLocationDao
import com.example.data.local.entity.OfflinePrayerScheduleEntity
import com.example.data.local.entity.PrayerScheduleSettingsEntity
import com.example.data.local.entity.UserLocationEntity
import com.example.data.model.AppSettings
import com.example.data.model.MonthlyPrayerDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflinePrayerRepository @Inject constructor(
    private val settingsDao: PrayerScheduleSettingsDao,
    private val locationDao: UserLocationDao,
    private val scheduleDao: OfflinePrayerScheduleDao,
    private val mosqueRepository: MosqueRepository
) {

    val settingsFlow: Flow<AppSettings> = settingsDao.getSettingsFlow()
        .map { entity -> entity?.toDomainModel() ?: AppSettings() }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    val savedLocationsFlow: Flow<List<UserLocationEntity>> = locationDao.getAllLocationsFlow()
        .flowOn(Dispatchers.IO)

    val selectedLocationFlow: Flow<UserLocationEntity?> = locationDao.getSelectedLocationFlow()
        .flowOn(Dispatchers.IO)

    suspend fun initializeDatabase() = withContext(Dispatchers.IO) {
        // 1. Ensure settings entity exists
        val currentSettings = settingsDao.getSettings()
        if (currentSettings == null) {
            settingsDao.insertOrUpdateSettings(
                PrayerScheduleSettingsEntity.fromDomainModel(AppSettings())
            )
        }

        // 2. Prepopulate 64 districts / default locations if empty
        val count = locationDao.getLocationCount()
        if (count == 0) {
            val locationEntities = mosqueRepository.districts.map { dist ->
                UserLocationEntity(
                    id = dist.id,
                    districtId = dist.id,
                    districtNameBn = dist.nameBn,
                    districtNameEn = dist.nameEn,
                    divisionNameBn = getDivisionForDistrict(dist.id),
                    latitude = getLatForDistrict(dist.id),
                    longitude = getLngForDistrict(dist.id),
                    fajrOffsetMinutes = dist.fajrOffsetMinutes,
                    dhuhrOffsetMinutes = dist.dhuhrOffsetMinutes,
                    asrOffsetMinutes = dist.asrOffsetMinutes,
                    maghribOffsetMinutes = dist.maghribOffsetMinutes,
                    ishaOffsetMinutes = dist.ishaOffsetMinutes,
                    isCurrentSelected = dist.id == (currentSettings?.selectedDistrictId ?: "dhaka"),
                    isGpsDerived = false
                )
            }
            locationDao.insertLocations(locationEntities)
        }

        // 3. Pre-cache current month's schedule for selected district
        val selectedId = currentSettings?.selectedDistrictId ?: "dhaka"
        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)
        val currentMonth = cal.get(Calendar.MONTH) + 1
        cacheMonthlySchedule(currentYear, currentMonth, selectedId)
    }

    suspend fun saveSettings(settings: AppSettings) = withContext(Dispatchers.IO) {
        settingsDao.insertOrUpdateSettings(PrayerScheduleSettingsEntity.fromDomainModel(settings))
        locationDao.setSelectedLocation(settings.selectedDistrictId)
    }

    suspend fun setSelectedDistrict(districtId: String) = withContext(Dispatchers.IO) {
        settingsDao.updateSelectedDistrict(districtId)
        locationDao.setSelectedLocation(districtId)

        // Cache schedules for this district
        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)
        val currentMonth = cal.get(Calendar.MONTH) + 1
        cacheMonthlySchedule(currentYear, currentMonth, districtId)
    }

    suspend fun saveCustomLocation(
        districtId: String,
        nameBn: String,
        nameEn: String,
        divisionBn: String,
        lat: Double,
        lng: Double,
        fajrOffset: Int,
        dhuhrOffset: Int,
        asrOffset: Int,
        maghribOffset: Int,
        ishaOffset: Int,
        isGps: Boolean = false,
        setAsCurrent: Boolean = true
    ) = withContext(Dispatchers.IO) {
        val entity = UserLocationEntity(
            id = districtId,
            districtId = districtId,
            districtNameBn = nameBn,
            districtNameEn = nameEn,
            divisionNameBn = divisionBn,
            latitude = lat,
            longitude = lng,
            fajrOffsetMinutes = fajrOffset,
            dhuhrOffsetMinutes = dhuhrOffset,
            asrOffsetMinutes = asrOffset,
            maghribOffsetMinutes = maghribOffset,
            ishaOffsetMinutes = ishaOffset,
            isCurrentSelected = setAsCurrent,
            isGpsDerived = isGps,
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
        locationDao.insertOrUpdateLocation(entity)
        if (setAsCurrent) {
            locationDao.setSelectedLocation(districtId)
            settingsDao.updateSelectedDistrict(districtId)
        }
    }

    suspend fun cacheMonthlySchedule(year: Int, month: Int, districtId: String) = withContext(Dispatchers.IO) {
        val calculatedDays = mosqueRepository.generateMonthlySchedule(year, month, districtId)
        val entities = calculatedDays.map { day ->
            OfflinePrayerScheduleEntity.fromMonthlyPrayerDay(day, year, month, districtId)
        }
        scheduleDao.insertMonthlySchedule(entities)
    }

    fun getOfflineMonthlyScheduleFlow(year: Int, month: Int, districtId: String): Flow<List<MonthlyPrayerDay>> {
        val todayCal = Calendar.getInstance()
        val todayYear = todayCal.get(Calendar.YEAR)
        val todayMonth = todayCal.get(Calendar.MONTH) + 1
        val todayDay = todayCal.get(Calendar.DAY_OF_MONTH)

        return scheduleDao.getMonthlyScheduleFlow(year, month, districtId).map { entities ->
            if (entities.isEmpty()) {
                // Generate and return fallback if cache not yet populated
                mosqueRepository.generateMonthlySchedule(year, month, districtId)
            } else {
                entities.map { entity ->
                    val isToday = (year == todayYear && month == todayMonth && entity.dayNumber == todayDay)
                    entity.toMonthlyPrayerDay(isToday = isToday)
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getOfflineDailySchedule(year: Int, month: Int, day: Int, districtId: String): MonthlyPrayerDay? = withContext(Dispatchers.IO) {
        val cached = scheduleDao.getDailySchedule(year, month, day, districtId)
        cached?.toMonthlyPrayerDay(isToday = true)
    }

    suspend fun resetAllSettings() = withContext(Dispatchers.IO) {
        settingsDao.insertOrUpdateSettings(PrayerScheduleSettingsEntity.fromDomainModel(AppSettings()))
        locationDao.setSelectedLocation("dhaka")
    }

    private fun getDivisionForDistrict(districtId: String): String {
        return when (districtId) {
            "dhaka", "gazipur", "narayanganj" -> "ঢাকা বিভাগ"
            "chittagong", "cumilla", "coxsbazar" -> "চট্টগ্রাম বিভাগ"
            "sylhet" -> "সিলেট বিভাগ"
            "rajshahi", "bogra" -> "রাজশাহী বিভাগ"
            "khulna", "jessore" -> "খুলনা বিভাগ"
            "barisal" -> "বরিশাল বিভাগ"
            "rangpur", "dinajpur" -> "রংপুর বিভাগ"
            "mymensingh" -> "ময়মনসিংহ বিভাগ"
            else -> "বাংলাদেশ"
        }
    }

    private fun getLatForDistrict(districtId: String): Double {
        return when (districtId) {
            "dhaka" -> 23.8103
            "chittagong" -> 22.3569
            "sylhet" -> 24.8949
            "rajshahi" -> 24.3745
            "khulna" -> 22.8456
            "barisal" -> 22.7010
            "rangpur" -> 25.7439
            "mymensingh" -> 24.7471
            "cumilla" -> 23.4607
            "gazipur" -> 23.9999
            "narayanganj" -> 23.6238
            "bogra" -> 24.8465
            "dinajpur" -> 25.6217
            "jessore" -> 23.1664
            "coxsbazar" -> 21.4272
            else -> 23.8103
        }
    }

    private fun getLngForDistrict(districtId: String): Double {
        return when (districtId) {
            "dhaka" -> 90.4125
            "chittagong" -> 91.7832
            "sylhet" -> 91.8687
            "rajshahi" -> 88.6042
            "khulna" -> 89.5403
            "barisal" -> 90.3535
            "rangpur" -> 89.2752
            "mymensingh" -> 90.4203
            "cumilla" -> 91.1809
            "gazipur" -> 90.4203
            "narayanganj" -> 90.5000
            "bogra" -> 89.3777
            "dinajpur" -> 88.6355
            "jessore" -> 89.2182
            "coxsbazar" -> 92.0058
            else -> 90.4125
        }
    }
}
