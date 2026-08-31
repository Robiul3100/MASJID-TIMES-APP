package com.example.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.data.local.dao.OfflinePrayerScheduleDao
import com.example.data.local.dao.PrayerScheduleSettingsDao
import com.example.data.local.dao.TasbihDao
import com.example.data.local.dao.UserLocationDao
import com.example.data.local.dao.UserQuestionDao
import com.example.data.local.entity.OfflinePrayerScheduleEntity
import com.example.data.local.entity.PrayerScheduleSettingsEntity
import com.example.data.local.entity.TasbihRecordEntity
import com.example.data.local.entity.UserLocationEntity
import com.example.data.local.entity.UserQuestionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Robust local SQLite-backed AppDatabase.
 * Implements reactive DAOs using StateFlow triggers and Android SQLite,
 * ensuring high reliability, instantaneous response, and complete offline persistence.
 */
class AppDatabase private constructor(context: Context) {

    private val dbHelper = DatabaseHelper(context.applicationContext)

    private val _settingsDao by lazy { SQLitePrayerScheduleSettingsDao(dbHelper) }
    private val _userLocationDao by lazy { SQLiteUserLocationDao(dbHelper) }
    private val _offlinePrayerScheduleDao by lazy { SQLiteOfflinePrayerScheduleDao(dbHelper) }
    private val _tasbihDao by lazy { SQLiteTasbihDao(dbHelper) }
    private val _userQuestionDao by lazy { SQLiteUserQuestionDao(dbHelper) }

    fun prayerScheduleSettingsDao(): PrayerScheduleSettingsDao = _settingsDao
    fun userLocationDao(): UserLocationDao = _userLocationDao
    fun offlinePrayerScheduleDao(): OfflinePrayerScheduleDao = _offlinePrayerScheduleDao
    fun tasbihDao(): TasbihDao = _tasbihDao
    fun userQuestionDao(): UserQuestionDao = _userQuestionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = AppDatabase(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseHelper(context: Context) : SQLiteOpenHelper(
        context,
        "mosque_offline_prayer.db",
        null,
        3
    ) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS prayer_schedule_settings (
                    id INTEGER PRIMARY KEY,
                    selectedDistrictId TEXT NOT NULL,
                    calculationMethod TEXT NOT NULL,
                    prayerReminderOffsetMinutes INTEGER NOT NULL,
                    isAdhanSoundEnabled INTEGER NOT NULL,
                    isPrayerNotificationEnabled INTEGER NOT NULL,
                    isNoticeNotificationEnabled INTEGER NOT NULL,
                    isEventNotificationEnabled INTEGER NOT NULL,
                    isJumahReminderEnabled INTEGER NOT NULL,
                    fontSizeScale TEXT NOT NULL,
                    language TEXT NOT NULL,
                    isNeonGlowActive INTEGER NOT NULL,
                    isAutoLocationEnabled INTEGER NOT NULL,
                    customFajrOffset INTEGER NOT NULL,
                    customDhuhrOffset INTEGER NOT NULL,
                    customAsrOffset INTEGER NOT NULL,
                    customMaghribOffset INTEGER NOT NULL,
                    customIshaOffset INTEGER NOT NULL,
                    lastUpdatedTimestamp INTEGER NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS user_locations (
                    id TEXT PRIMARY KEY,
                    districtId TEXT NOT NULL,
                    districtNameBn TEXT NOT NULL,
                    districtNameEn TEXT NOT NULL,
                    divisionNameBn TEXT NOT NULL,
                    latitude REAL NOT NULL,
                    longitude REAL NOT NULL,
                    fajrOffsetMinutes INTEGER NOT NULL,
                    dhuhrOffsetMinutes INTEGER NOT NULL,
                    asrOffsetMinutes INTEGER NOT NULL,
                    maghribOffsetMinutes INTEGER NOT NULL,
                    ishaOffsetMinutes INTEGER NOT NULL,
                    isCurrentSelected INTEGER NOT NULL,
                    isGpsDerived INTEGER NOT NULL,
                    lastUpdatedTimestamp INTEGER NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS offline_prayer_schedules (
                    year INTEGER NOT NULL,
                    month INTEGER NOT NULL,
                    dayNumber INTEGER NOT NULL,
                    districtId TEXT NOT NULL,
                    bengaliDate TEXT NOT NULL,
                    hijriDate TEXT NOT NULL,
                    gregorianDate TEXT NOT NULL,
                    dayName TEXT NOT NULL,
                    fajrAzan TEXT NOT NULL,
                    fajrIqamah TEXT NOT NULL,
                    sunrise TEXT NOT NULL,
                    dhuhrAzan TEXT NOT NULL,
                    dhuhrIqamah TEXT NOT NULL,
                    asrAzan TEXT NOT NULL,
                    asrIqamah TEXT NOT NULL,
                    maghribAzan TEXT NOT NULL,
                    maghribIqamah TEXT NOT NULL,
                    ishaAzan TEXT NOT NULL,
                    ishaIqamah TEXT NOT NULL,
                    isFriday INTEGER NOT NULL,
                    cachedTimestamp INTEGER NOT NULL,
                    PRIMARY KEY (year, month, dayNumber, districtId)
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS tasbih_records (
                    id TEXT PRIMARY KEY,
                    dhikrId TEXT NOT NULL,
                    dhikrNameBn TEXT NOT NULL,
                    count INTEGER NOT NULL,
                    target INTEGER NOT NULL,
                    dateString TEXT NOT NULL,
                    timestamp INTEGER NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS user_questions (
                    id TEXT PRIMARY KEY,
                    senderName TEXT NOT NULL,
                    senderPhone TEXT NOT NULL,
                    categoryName TEXT NOT NULL,
                    questionText TEXT NOT NULL,
                    isPrivate INTEGER NOT NULL,
                    submissionDate INTEGER NOT NULL,
                    status TEXT NOT NULL,
                    replyText TEXT NOT NULL DEFAULT '',
                    repliedBy TEXT NOT NULL DEFAULT '',
                    replyDateBn TEXT NOT NULL DEFAULT ''
                )
                """.trimIndent()
            )
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS prayer_schedule_settings")
            db.execSQL("DROP TABLE IF EXISTS user_locations")
            db.execSQL("DROP TABLE IF EXISTS offline_prayer_schedules")
            db.execSQL("DROP TABLE IF EXISTS tasbih_records")
            db.execSQL("DROP TABLE IF EXISTS user_questions")
            onCreate(db)
        }
    }

    // ------------------------------------------------------------------------------------
    // SQLite DAOs
    // ------------------------------------------------------------------------------------

    private class SQLitePrayerScheduleSettingsDao(
        private val dbHelper: DatabaseHelper
    ) : PrayerScheduleSettingsDao {

        private val _flow = MutableStateFlow<PrayerScheduleSettingsEntity?>(null)

        init {
            loadFromDb()
        }

        private fun loadFromDb(): PrayerScheduleSettingsEntity? {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM prayer_schedule_settings WHERE id = 1 LIMIT 1", null)
            val entity = cursor.use {
                if (it.moveToFirst()) parseSettings(it) else null
            }
            _flow.value = entity
            return entity
        }

        private fun parseSettings(c: Cursor): PrayerScheduleSettingsEntity {
            return PrayerScheduleSettingsEntity(
                id = c.getInt(c.getColumnIndexOrThrow("id")),
                selectedDistrictId = c.getString(c.getColumnIndexOrThrow("selectedDistrictId")),
                calculationMethod = c.getString(c.getColumnIndexOrThrow("calculationMethod")),
                prayerReminderOffsetMinutes = c.getInt(c.getColumnIndexOrThrow("prayerReminderOffsetMinutes")),
                isAdhanSoundEnabled = c.getInt(c.getColumnIndexOrThrow("isAdhanSoundEnabled")) == 1,
                isPrayerNotificationEnabled = c.getInt(c.getColumnIndexOrThrow("isPrayerNotificationEnabled")) == 1,
                isNoticeNotificationEnabled = c.getInt(c.getColumnIndexOrThrow("isNoticeNotificationEnabled")) == 1,
                isEventNotificationEnabled = c.getInt(c.getColumnIndexOrThrow("isEventNotificationEnabled")) == 1,
                isJumahReminderEnabled = c.getInt(c.getColumnIndexOrThrow("isJumahReminderEnabled")) == 1,
                fontSizeScale = c.getString(c.getColumnIndexOrThrow("fontSizeScale")),
                language = c.getString(c.getColumnIndexOrThrow("language")),
                isNeonGlowActive = c.getInt(c.getColumnIndexOrThrow("isNeonGlowActive")) == 1,
                isAutoLocationEnabled = c.getInt(c.getColumnIndexOrThrow("isAutoLocationEnabled")) == 1,
                customFajrOffset = c.getInt(c.getColumnIndexOrThrow("customFajrOffset")),
                customDhuhrOffset = c.getInt(c.getColumnIndexOrThrow("customDhuhrOffset")),
                customAsrOffset = c.getInt(c.getColumnIndexOrThrow("customAsrOffset")),
                customMaghribOffset = c.getInt(c.getColumnIndexOrThrow("customMaghribOffset")),
                customIshaOffset = c.getInt(c.getColumnIndexOrThrow("customIshaOffset")),
                lastUpdatedTimestamp = c.getLong(c.getColumnIndexOrThrow("lastUpdatedTimestamp"))
            )
        }

        override fun getSettingsFlow(): Flow<PrayerScheduleSettingsEntity?> = _flow.asStateFlow()

        override suspend fun getSettings(): PrayerScheduleSettingsEntity? = withContext(Dispatchers.IO) {
            loadFromDb()
        }

        override suspend fun insertOrUpdateSettings(settings: PrayerScheduleSettingsEntity) = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("id", settings.id)
                put("selectedDistrictId", settings.selectedDistrictId)
                put("calculationMethod", settings.calculationMethod)
                put("prayerReminderOffsetMinutes", settings.prayerReminderOffsetMinutes)
                put("isAdhanSoundEnabled", if (settings.isAdhanSoundEnabled) 1 else 0)
                put("isPrayerNotificationEnabled", if (settings.isPrayerNotificationEnabled) 1 else 0)
                put("isNoticeNotificationEnabled", if (settings.isNoticeNotificationEnabled) 1 else 0)
                put("isEventNotificationEnabled", if (settings.isEventNotificationEnabled) 1 else 0)
                put("isJumahReminderEnabled", if (settings.isJumahReminderEnabled) 1 else 0)
                put("fontSizeScale", settings.fontSizeScale)
                put("language", settings.language)
                put("isNeonGlowActive", if (settings.isNeonGlowActive) 1 else 0)
                put("isAutoLocationEnabled", if (settings.isAutoLocationEnabled) 1 else 0)
                put("customFajrOffset", settings.customFajrOffset)
                put("customDhuhrOffset", settings.customDhuhrOffset)
                put("customAsrOffset", settings.customAsrOffset)
                put("customMaghribOffset", settings.customMaghribOffset)
                put("customIshaOffset", settings.customIshaOffset)
                put("lastUpdatedTimestamp", settings.lastUpdatedTimestamp)
            }
            db.insertWithOnConflict("prayer_schedule_settings", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            _flow.value = settings
        }

        override suspend fun updateSelectedDistrict(districtId: String, timestamp: Long) = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("selectedDistrictId", districtId)
                put("lastUpdatedTimestamp", timestamp)
            }
            db.update("prayer_schedule_settings", values, "id = 1", null)
            loadFromDb()
            Unit
        }

        override suspend fun updateCalculationMethod(method: String, timestamp: Long) = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("calculationMethod", method)
                put("lastUpdatedTimestamp", timestamp)
            }
            db.update("prayer_schedule_settings", values, "id = 1", null)
            loadFromDb()
            Unit
        }

        override suspend fun updatePrayerNotification(enabled: Boolean, timestamp: Long) = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("isPrayerNotificationEnabled", if (enabled) 1 else 0)
                put("lastUpdatedTimestamp", timestamp)
            }
            db.update("prayer_schedule_settings", values, "id = 1", null)
            loadFromDb()
            Unit
        }

        override suspend fun clearSettings() = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            db.delete("prayer_schedule_settings", null, null)
            _flow.value = null
            Unit
        }
    }

    private class SQLiteUserLocationDao(
        private val dbHelper: DatabaseHelper
    ) : UserLocationDao {

        private val _allLocationsFlow = MutableStateFlow<List<UserLocationEntity>>(emptyList())
        private val _selectedLocationFlow = MutableStateFlow<UserLocationEntity?>(null)

        init {
            refreshFlows()
        }

        private fun refreshFlows() {
            val db = dbHelper.readableDatabase
            val list = mutableListOf<UserLocationEntity>()
            val cursor = db.rawQuery("SELECT * FROM user_locations ORDER BY isCurrentSelected DESC, districtNameBn ASC", null)
            cursor.use {
                while (it.moveToNext()) {
                    list.add(parseLocation(it))
                }
            }
            _allLocationsFlow.value = list
            _selectedLocationFlow.value = list.firstOrNull { it.isCurrentSelected }
        }

        private fun parseLocation(c: Cursor): UserLocationEntity {
            return UserLocationEntity(
                id = c.getString(c.getColumnIndexOrThrow("id")),
                districtId = c.getString(c.getColumnIndexOrThrow("districtId")),
                districtNameBn = c.getString(c.getColumnIndexOrThrow("districtNameBn")),
                districtNameEn = c.getString(c.getColumnIndexOrThrow("districtNameEn")),
                divisionNameBn = c.getString(c.getColumnIndexOrThrow("divisionNameBn")),
                latitude = c.getDouble(c.getColumnIndexOrThrow("latitude")),
                longitude = c.getDouble(c.getColumnIndexOrThrow("longitude")),
                fajrOffsetMinutes = c.getInt(c.getColumnIndexOrThrow("fajrOffsetMinutes")),
                dhuhrOffsetMinutes = c.getInt(c.getColumnIndexOrThrow("dhuhrOffsetMinutes")),
                asrOffsetMinutes = c.getInt(c.getColumnIndexOrThrow("asrOffsetMinutes")),
                maghribOffsetMinutes = c.getInt(c.getColumnIndexOrThrow("maghribOffsetMinutes")),
                ishaOffsetMinutes = c.getInt(c.getColumnIndexOrThrow("ishaOffsetMinutes")),
                isCurrentSelected = c.getInt(c.getColumnIndexOrThrow("isCurrentSelected")) == 1,
                isGpsDerived = c.getInt(c.getColumnIndexOrThrow("isGpsDerived")) == 1,
                lastUpdatedTimestamp = c.getLong(c.getColumnIndexOrThrow("lastUpdatedTimestamp"))
            )
        }

        override fun getAllLocationsFlow(): Flow<List<UserLocationEntity>> = _allLocationsFlow.asStateFlow()

        override fun getSelectedLocationFlow(): Flow<UserLocationEntity?> = _selectedLocationFlow.asStateFlow()

        override suspend fun getSelectedLocation(): UserLocationEntity? = withContext(Dispatchers.IO) {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM user_locations WHERE isCurrentSelected = 1 LIMIT 1", null)
            cursor.use {
                if (it.moveToFirst()) parseLocation(it) else null
            }
        }

        override suspend fun getLocationById(districtId: String): UserLocationEntity? = withContext(Dispatchers.IO) {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM user_locations WHERE districtId = ? LIMIT 1", arrayOf(districtId))
            cursor.use {
                if (it.moveToFirst()) parseLocation(it) else null
            }
        }

        override suspend fun getLocationCount(): Int = withContext(Dispatchers.IO) {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT COUNT(*) FROM user_locations", null)
            cursor.use {
                if (it.moveToFirst()) it.getInt(0) else 0
            }
        }

        override suspend fun insertLocations(locations: List<UserLocationEntity>) = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            db.beginTransaction()
            try {
                for (loc in locations) {
                    val values = ContentValues().apply {
                        put("id", loc.id)
                        put("districtId", loc.districtId)
                        put("districtNameBn", loc.districtNameBn)
                        put("districtNameEn", loc.districtNameEn)
                        put("divisionNameBn", loc.divisionNameBn)
                        put("latitude", loc.latitude)
                        put("longitude", loc.longitude)
                        put("fajrOffsetMinutes", loc.fajrOffsetMinutes)
                        put("dhuhrOffsetMinutes", loc.dhuhrOffsetMinutes)
                        put("asrOffsetMinutes", loc.asrOffsetMinutes)
                        put("maghribOffsetMinutes", loc.maghribOffsetMinutes)
                        put("ishaOffsetMinutes", loc.ishaOffsetMinutes)
                        put("isCurrentSelected", if (loc.isCurrentSelected) 1 else 0)
                        put("isGpsDerived", if (loc.isGpsDerived) 1 else 0)
                        put("lastUpdatedTimestamp", loc.lastUpdatedTimestamp)
                    }
                    db.insertWithOnConflict("user_locations", null, values, SQLiteDatabase.CONFLICT_REPLACE)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
            refreshFlows()
        }

        override suspend fun insertOrUpdateLocation(location: UserLocationEntity) = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("id", location.id)
                put("districtId", location.districtId)
                put("districtNameBn", location.districtNameBn)
                put("districtNameEn", location.districtNameEn)
                put("divisionNameBn", location.divisionNameBn)
                put("latitude", location.latitude)
                put("longitude", location.longitude)
                put("fajrOffsetMinutes", location.fajrOffsetMinutes)
                put("dhuhrOffsetMinutes", location.dhuhrOffsetMinutes)
                put("asrOffsetMinutes", location.asrOffsetMinutes)
                put("maghribOffsetMinutes", location.maghribOffsetMinutes)
                put("ishaOffsetMinutes", location.ishaOffsetMinutes)
                put("isCurrentSelected", if (location.isCurrentSelected) 1 else 0)
                put("isGpsDerived", if (location.isGpsDerived) 1 else 0)
                put("lastUpdatedTimestamp", location.lastUpdatedTimestamp)
            }
            db.insertWithOnConflict("user_locations", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            refreshFlows()
        }

        override suspend fun clearAllSelectedFlags() = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply { put("isCurrentSelected", 0) }
            db.update("user_locations", values, null, null)
            refreshFlows()
            Unit
        }

        override suspend fun markLocationAsSelected(districtId: String, timestamp: Long) = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("isCurrentSelected", 1)
                put("lastUpdatedTimestamp", timestamp)
            }
            db.update("user_locations", values, "districtId = ?", arrayOf(districtId))
            refreshFlows()
            Unit
        }

        override suspend fun deleteLocation(districtId: String) = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            db.delete("user_locations", "districtId = ?", arrayOf(districtId))
            refreshFlows()
            Unit
        }

        override suspend fun clearAllLocations() = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            db.delete("user_locations", null, null)
            refreshFlows()
            Unit
        }
    }

    private class SQLiteOfflinePrayerScheduleDao(
        private val dbHelper: DatabaseHelper
    ) : OfflinePrayerScheduleDao {

        private fun parseSchedule(c: Cursor): OfflinePrayerScheduleEntity {
            return OfflinePrayerScheduleEntity(
                year = c.getInt(c.getColumnIndexOrThrow("year")),
                month = c.getInt(c.getColumnIndexOrThrow("month")),
                dayNumber = c.getInt(c.getColumnIndexOrThrow("dayNumber")),
                districtId = c.getString(c.getColumnIndexOrThrow("districtId")),
                bengaliDate = c.getString(c.getColumnIndexOrThrow("bengaliDate")),
                hijriDate = c.getString(c.getColumnIndexOrThrow("hijriDate")),
                gregorianDate = c.getString(c.getColumnIndexOrThrow("gregorianDate")),
                dayName = c.getString(c.getColumnIndexOrThrow("dayName")),
                fajrAzan = c.getString(c.getColumnIndexOrThrow("fajrAzan")),
                fajrIqamah = c.getString(c.getColumnIndexOrThrow("fajrIqamah")),
                sunrise = c.getString(c.getColumnIndexOrThrow("sunrise")),
                dhuhrAzan = c.getString(c.getColumnIndexOrThrow("dhuhrAzan")),
                dhuhrIqamah = c.getString(c.getColumnIndexOrThrow("dhuhrIqamah")),
                asrAzan = c.getString(c.getColumnIndexOrThrow("asrAzan")),
                asrIqamah = c.getString(c.getColumnIndexOrThrow("asrIqamah")),
                maghribAzan = c.getString(c.getColumnIndexOrThrow("maghribAzan")),
                maghribIqamah = c.getString(c.getColumnIndexOrThrow("maghribIqamah")),
                ishaAzan = c.getString(c.getColumnIndexOrThrow("ishaAzan")),
                ishaIqamah = c.getString(c.getColumnIndexOrThrow("ishaIqamah")),
                isFriday = c.getInt(c.getColumnIndexOrThrow("isFriday")) == 1,
                cachedTimestamp = c.getLong(c.getColumnIndexOrThrow("cachedTimestamp"))
            )
        }

        override fun getMonthlyScheduleFlow(year: Int, month: Int, districtId: String): Flow<List<OfflinePrayerScheduleEntity>> {
            val list = fetchMonthlySchedule(year, month, districtId)
            return MutableStateFlow(list).asStateFlow()
        }

        private fun fetchMonthlySchedule(year: Int, month: Int, districtId: String): List<OfflinePrayerScheduleEntity> {
            val db = dbHelper.readableDatabase
            val list = mutableListOf<OfflinePrayerScheduleEntity>()
            val cursor = db.rawQuery(
                "SELECT * FROM offline_prayer_schedules WHERE year = ? AND month = ? AND districtId = ? ORDER BY dayNumber ASC",
                arrayOf(year.toString(), month.toString(), districtId)
            )
            cursor.use {
                while (it.moveToNext()) {
                    list.add(parseSchedule(it))
                }
            }
            return list
        }

        override suspend fun getMonthlySchedule(year: Int, month: Int, districtId: String): List<OfflinePrayerScheduleEntity> = withContext(Dispatchers.IO) {
            fetchMonthlySchedule(year, month, districtId)
        }

        override suspend fun getDailySchedule(year: Int, month: Int, dayNumber: Int, districtId: String): OfflinePrayerScheduleEntity? = withContext(Dispatchers.IO) {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery(
                "SELECT * FROM offline_prayer_schedules WHERE year = ? AND month = ? AND dayNumber = ? AND districtId = ? LIMIT 1",
                arrayOf(year.toString(), month.toString(), dayNumber.toString(), districtId)
            )
            cursor.use {
                if (it.moveToFirst()) parseSchedule(it) else null
            }
        }

        override suspend fun insertMonthlySchedule(schedules: List<OfflinePrayerScheduleEntity>) = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            db.beginTransaction()
            try {
                for (s in schedules) {
                    val values = ContentValues().apply {
                        put("year", s.year)
                        put("month", s.month)
                        put("dayNumber", s.dayNumber)
                        put("districtId", s.districtId)
                        put("bengaliDate", s.bengaliDate)
                        put("hijriDate", s.hijriDate)
                        put("gregorianDate", s.gregorianDate)
                        put("dayName", s.dayName)
                        put("fajrAzan", s.fajrAzan)
                        put("fajrIqamah", s.fajrIqamah)
                        put("sunrise", s.sunrise)
                        put("dhuhrAzan", s.dhuhrAzan)
                        put("dhuhrIqamah", s.dhuhrIqamah)
                        put("asrAzan", s.asrAzan)
                        put("asrIqamah", s.asrIqamah)
                        put("maghribAzan", s.maghribAzan)
                        put("maghribIqamah", s.maghribIqamah)
                        put("ishaAzan", s.ishaAzan)
                        put("ishaIqamah", s.ishaIqamah)
                        put("isFriday", if (s.isFriday) 1 else 0)
                        put("cachedTimestamp", s.cachedTimestamp)
                    }
                    db.insertWithOnConflict("offline_prayer_schedules", null, values, SQLiteDatabase.CONFLICT_REPLACE)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }

        override suspend fun deleteMonthlySchedule(year: Int, month: Int, districtId: String) = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            db.delete(
                "offline_prayer_schedules",
                "year = ? AND month = ? AND districtId = ?",
                arrayOf(year.toString(), month.toString(), districtId)
            )
            Unit
        }

        override suspend fun clearAllCachedSchedules() = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            db.delete("offline_prayer_schedules", null, null)
            Unit
        }
    }

    private class SQLiteTasbihDao(
        private val dbHelper: DatabaseHelper
    ) : TasbihDao {

        private val _recordsFlow = MutableStateFlow<List<TasbihRecordEntity>>(emptyList())

        init {
            refreshFlow()
        }

        private fun refreshFlow() {
            val db = dbHelper.readableDatabase
            val list = mutableListOf<TasbihRecordEntity>()
            val cursor = db.rawQuery("SELECT * FROM tasbih_records ORDER BY timestamp DESC", null)
            cursor.use {
                while (it.moveToNext()) {
                    list.add(
                        TasbihRecordEntity(
                            id = it.getString(it.getColumnIndexOrThrow("id")),
                            dhikrId = it.getString(it.getColumnIndexOrThrow("dhikrId")),
                            dhikrNameBn = it.getString(it.getColumnIndexOrThrow("dhikrNameBn")),
                            count = it.getInt(it.getColumnIndexOrThrow("count")),
                            target = it.getInt(it.getColumnIndexOrThrow("target")),
                            dateString = it.getString(it.getColumnIndexOrThrow("dateString")),
                            timestamp = it.getLong(it.getColumnIndexOrThrow("timestamp"))
                        )
                    )
                }
            }
            _recordsFlow.value = list
        }

        override fun getAllRecordsFlow(): Flow<List<TasbihRecordEntity>> = _recordsFlow.asStateFlow()

        override suspend fun insertRecord(record: TasbihRecordEntity) = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("id", record.id)
                put("dhikrId", record.dhikrId)
                put("dhikrNameBn", record.dhikrNameBn)
                put("count", record.count)
                put("target", record.target)
                put("dateString", record.dateString)
                put("timestamp", record.timestamp)
            }
            db.insertWithOnConflict("tasbih_records", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            refreshFlow()
        }

        override suspend fun deleteRecord(id: String) = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            db.delete("tasbih_records", "id = ?", arrayOf(id))
            refreshFlow()
            Unit
        }

        override suspend fun clearAllRecords() = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            db.delete("tasbih_records", null, null)
            refreshFlow()
            Unit
        }
    }

    private class SQLiteUserQuestionDao(
        private val dbHelper: DatabaseHelper
    ) : UserQuestionDao {

        private val _questionsFlow = MutableStateFlow<List<UserQuestionEntity>>(emptyList())

        init {
            refreshFlow()
        }

        private fun refreshFlow() {
            val db = dbHelper.readableDatabase
            val list = mutableListOf<UserQuestionEntity>()
            val cursor = db.rawQuery("SELECT * FROM user_questions ORDER BY submissionDate DESC", null)
            cursor.use {
                while (it.moveToNext()) {
                    val replyTextIdx = it.getColumnIndex("replyText")
                    val repliedByIdx = it.getColumnIndex("repliedBy")
                    val replyDateBnIdx = it.getColumnIndex("replyDateBn")

                    val rText = if (replyTextIdx >= 0) it.getString(replyTextIdx) ?: "" else ""
                    val rBy = if (repliedByIdx >= 0) it.getString(repliedByIdx) ?: "" else ""
                    val rDate = if (replyDateBnIdx >= 0) it.getString(replyDateBnIdx) ?: "" else ""

                    list.add(
                        UserQuestionEntity(
                            id = it.getString(it.getColumnIndexOrThrow("id")),
                            senderName = it.getString(it.getColumnIndexOrThrow("senderName")),
                            senderPhone = it.getString(it.getColumnIndexOrThrow("senderPhone")),
                            categoryName = it.getString(it.getColumnIndexOrThrow("categoryName")),
                            questionText = it.getString(it.getColumnIndexOrThrow("questionText")),
                            isPrivate = it.getInt(it.getColumnIndexOrThrow("isPrivate")) == 1,
                            submissionDate = it.getLong(it.getColumnIndexOrThrow("submissionDate")),
                            status = it.getString(it.getColumnIndexOrThrow("status")),
                            replyText = rText,
                            repliedBy = rBy,
                            replyDateBn = rDate
                        )
                    )
                }
            }
            _questionsFlow.value = list
        }

        override fun getAllUserQuestionsFlow(): Flow<List<UserQuestionEntity>> = _questionsFlow.asStateFlow()

        override suspend fun insertUserQuestion(question: UserQuestionEntity) = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("id", question.id)
                put("senderName", question.senderName)
                put("senderPhone", question.senderPhone)
                put("categoryName", question.categoryName)
                put("questionText", question.questionText)
                put("isPrivate", if (question.isPrivate) 1 else 0)
                put("submissionDate", question.submissionDate)
                put("status", question.status)
                put("replyText", question.replyText)
                put("repliedBy", question.repliedBy)
                put("replyDateBn", question.replyDateBn)
            }
            db.insertWithOnConflict("user_questions", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            refreshFlow()
        }

        override suspend fun updateQuestionReply(
            id: String,
            replyText: String,
            replyDateBn: String,
            repliedBy: String
        ) = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("replyText", replyText)
                put("replyDateBn", replyDateBn)
                put("repliedBy", repliedBy)
                put("status", "উত্তর সম্পন্ন")
            }
            db.update("user_questions", values, "id = ?", arrayOf(id))
            refreshFlow()
            Unit
        }

        override suspend fun getQuestionById(id: String): UserQuestionEntity? = withContext(Dispatchers.IO) {
            val db = dbHelper.readableDatabase
            var result: UserQuestionEntity? = null
            val cursor = db.rawQuery("SELECT * FROM user_questions WHERE id = ? LIMIT 1", arrayOf(id))
            cursor.use {
                if (it.moveToNext()) {
                    val replyTextIdx = it.getColumnIndex("replyText")
                    val repliedByIdx = it.getColumnIndex("repliedBy")
                    val replyDateBnIdx = it.getColumnIndex("replyDateBn")

                    val rText = if (replyTextIdx >= 0) it.getString(replyTextIdx) ?: "" else ""
                    val rBy = if (repliedByIdx >= 0) it.getString(repliedByIdx) ?: "" else ""
                    val rDate = if (replyDateBnIdx >= 0) it.getString(replyDateBnIdx) ?: "" else ""

                    result = UserQuestionEntity(
                        id = it.getString(it.getColumnIndexOrThrow("id")),
                        senderName = it.getString(it.getColumnIndexOrThrow("senderName")),
                        senderPhone = it.getString(it.getColumnIndexOrThrow("senderPhone")),
                        categoryName = it.getString(it.getColumnIndexOrThrow("categoryName")),
                        questionText = it.getString(it.getColumnIndexOrThrow("questionText")),
                        isPrivate = it.getInt(it.getColumnIndexOrThrow("isPrivate")) == 1,
                        submissionDate = it.getLong(it.getColumnIndexOrThrow("submissionDate")),
                        status = it.getString(it.getColumnIndexOrThrow("status")),
                        replyText = rText,
                        repliedBy = rBy,
                        replyDateBn = rDate
                    )
                }
            }
            result
        }

        override suspend fun deleteUserQuestion(id: String) = withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            db.delete("user_questions", "id = ?", arrayOf(id))
            refreshFlow()
            Unit
        }
    }
}
