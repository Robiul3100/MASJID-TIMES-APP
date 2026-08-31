package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entity.PrayerScheduleSettingsEntity
import com.example.data.local.entity.UserLocationEntity
import com.example.data.model.AppSettings
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("জামে মসজিদ", appName)
    }

    @Test
    fun `test prayer settings entity persistence in Room`() = runBlocking {
        val dao = database.prayerScheduleSettingsDao()
        val defaultSettings = AppSettings(selectedDistrictId = "chittagong")
        val entity = PrayerScheduleSettingsEntity.fromDomainModel(defaultSettings)
        
        dao.insertOrUpdateSettings(entity)
        val loaded = dao.getSettings()

        assertNotNull(loaded)
        assertEquals("chittagong", loaded?.selectedDistrictId)
    }

    @Test
    fun `test user location entity persistence and selection`() = runBlocking {
        val dao = database.userLocationDao()
        val location = UserLocationEntity(
            id = "dhaka",
            districtId = "dhaka",
            districtNameBn = "ঢাকা",
            districtNameEn = "Dhaka",
            isCurrentSelected = true
        )
        dao.insertOrUpdateLocation(location)
        val selected = dao.getSelectedLocation()

        assertNotNull(selected)
        assertEquals("dhaka", selected?.districtId)
        assertEquals("ঢাকা", selected?.districtNameBn)
    }
}

