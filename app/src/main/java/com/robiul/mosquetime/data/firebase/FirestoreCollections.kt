package com.robiul.mosquetime.data.firebase

/**
 * Dynamic Firestore collection and document path builder with multi-mosque isolation.
 */
object FirestoreCollections {
    @Volatile
    var activeMosqueId: String = "main_mosque"

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
    const val MOSQUE_CONFIGS = "mosqueConfigs"

    fun mosqueDoc(mosqueId: String = activeMosqueId) = "$MOSQUES/$mosqueId"
    fun noticesCollection(mosqueId: String = activeMosqueId) = "$MOSQUES/$mosqueId/$NOTICES"
    fun notificationsCollection(mosqueId: String = activeMosqueId) = "$MOSQUES/$mosqueId/$NOTIFICATIONS"
    fun mealsCollection(mosqueId: String = activeMosqueId) = "$MOSQUES/$mosqueId/$MEAL_SCHEDULES"
    fun householdsCollection(mosqueId: String = activeMosqueId) = "$MOSQUES/$mosqueId/$HOUSEHOLDS"
    fun eventsCollection(mosqueId: String = activeMosqueId) = "$MOSQUES/$mosqueId/$EVENTS"
    fun duasCollection(mosqueId: String = activeMosqueId) = "$MOSQUES/$mosqueId/$DUAS"
    fun committeeDoc(mosqueId: String = activeMosqueId) = "$MOSQUES/$mosqueId/$COMMITTEE/list"
    fun janazaCollection(mosqueId: String = activeMosqueId) = "$MOSQUES/$mosqueId/janaza"
    fun emergencyCollection(mosqueId: String = activeMosqueId) = "$MOSQUES/$mosqueId/$EMERGENCY"
    fun prayersDoc(mosqueId: String = activeMosqueId) = "$MOSQUES/$mosqueId/$PRAYER_SCHEDULES/current"
    fun profileDoc(mosqueId: String = activeMosqueId) = "$MOSQUES/$mosqueId/$PROFILE/info"
    fun emergencyDoc(mosqueId: String = activeMosqueId) = "$MOSQUES/$mosqueId/$EMERGENCY/current"
    fun fcmTokensCollection(mosqueId: String = activeMosqueId) = "$MOSQUES/$mosqueId/fcm_devices/tokens"
    fun fcmBroadcastsCollection(mosqueId: String = activeMosqueId) = "$MOSQUES/$mosqueId/fcm_broadcasts"
    fun activeSessionsCollection(mosqueId: String = activeMosqueId) = "$MOSQUES/$mosqueId/active_sessions"
}
