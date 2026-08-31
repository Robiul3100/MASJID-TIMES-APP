package com.example.data.firebase

object FirestoreCollections {
    const val DEFAULT_MOSQUE_ID = "main_mosque"

    const val MOSQUES = "mosques"
    const val PROFILE = "profile"
    const val PRAYER_SCHEDULES = "prayerSchedules"
    const val MEAL_SCHEDULES = "mealSchedules"
    const val HOUSEHOLDS = "households"
    const val NOTICES = "notices"
    const val NOTIFICATIONS = "notifications"
    const val EVENTS = "events"
    const val DUAS = "duas"
    const val COMMITTEE = "committee"
    const val GALLERY = "gallery"
    const val EMERGENCY = "emergencyAnnouncements"
    const val RAMADAN = "ramadan"

    const val ADMIN_USERS = "adminUsers"
    const val AUDIT_LOGS = "auditLogs"

    fun mosqueDoc(mosqueId: String = DEFAULT_MOSQUE_ID) = "$MOSQUES/$mosqueId"
    fun noticesCollection(mosqueId: String = DEFAULT_MOSQUE_ID) = "$MOSQUES/$mosqueId/$NOTICES"
    fun notificationsCollection(mosqueId: String = DEFAULT_MOSQUE_ID) = "$MOSQUES/$mosqueId/$NOTIFICATIONS"
    fun mealsCollection(mosqueId: String = DEFAULT_MOSQUE_ID) = "$MOSQUES/$mosqueId/$MEAL_SCHEDULES"
    fun householdsCollection(mosqueId: String = DEFAULT_MOSQUE_ID) = "$MOSQUES/$mosqueId/$HOUSEHOLDS"
    fun eventsCollection(mosqueId: String = DEFAULT_MOSQUE_ID) = "$MOSQUES/$mosqueId/$EVENTS"
    fun janazaCollection(mosqueId: String = DEFAULT_MOSQUE_ID) = "$MOSQUES/$mosqueId/janaza"
    fun emergencyCollection(mosqueId: String = DEFAULT_MOSQUE_ID) = "$MOSQUES/$mosqueId/$EMERGENCY"
    fun prayersDoc(mosqueId: String = DEFAULT_MOSQUE_ID) = "$MOSQUES/$mosqueId/$PRAYER_SCHEDULES/current"
    fun profileDoc(mosqueId: String = DEFAULT_MOSQUE_ID) = "$MOSQUES/$mosqueId/$PROFILE/info"
    fun emergencyDoc(mosqueId: String = DEFAULT_MOSQUE_ID) = "$MOSQUES/$mosqueId/$EMERGENCY/current"
}
