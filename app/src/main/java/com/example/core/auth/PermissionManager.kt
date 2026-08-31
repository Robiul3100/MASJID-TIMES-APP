package com.example.core.auth

object PermissionManager {
    fun canManageMosqueProfile(user: AdminUser?): Boolean =
        user != null && user.isActive && canManageMosqueProfile(user.role)

    fun canManageMosqueProfile(role: AdminRole): Boolean {
        return role == AdminRole.SUPER_ADMIN || role == AdminRole.ADMIN
    }

    fun canManagePrayerTimes(user: AdminUser?): Boolean =
        user != null && user.isActive && canManagePrayerTimes(user.role)

    fun canManagePrayerTimes(role: AdminRole): Boolean {
        return role == AdminRole.SUPER_ADMIN || role == AdminRole.ADMIN || role == AdminRole.IMAM
    }

    fun canManageMealSchedules(user: AdminUser?): Boolean =
        user != null && user.isActive && canManageMealSchedules(user.role)

    fun canManageMealSchedules(role: AdminRole): Boolean {
        return role == AdminRole.SUPER_ADMIN || role == AdminRole.ADMIN || role == AdminRole.IMAM
    }

    fun canManageNotices(user: AdminUser?): Boolean =
        user != null && user.isActive && canManageNotices(user.role)

    fun canManageNotices(role: AdminRole): Boolean {
        return role == AdminRole.SUPER_ADMIN || role == AdminRole.ADMIN || role == AdminRole.IMAM || role == AdminRole.EDITOR
    }

    fun canSendNotifications(user: AdminUser?): Boolean =
        user != null && user.isActive && canSendNotifications(user.role)

    fun canSendNotifications(role: AdminRole): Boolean {
        return role == AdminRole.SUPER_ADMIN || role == AdminRole.ADMIN || role == AdminRole.IMAM
    }

    fun canSendEmergencyAnnouncement(user: AdminUser?): Boolean =
        user != null && user.isActive && canSendEmergencyAnnouncement(user.role)

    fun canSendEmergencyAnnouncement(role: AdminRole): Boolean {
        return role == AdminRole.SUPER_ADMIN || role == AdminRole.ADMIN || role == AdminRole.IMAM
    }

    fun canManageEvents(user: AdminUser?): Boolean =
        user != null && user.isActive && canManageEvents(user.role)

    fun canManageEvents(role: AdminRole): Boolean {
        return role == AdminRole.SUPER_ADMIN || role == AdminRole.ADMIN || role == AdminRole.EDITOR
    }

    fun canManageDuas(user: AdminUser?): Boolean =
        user != null && user.isActive && canManageDuas(user.role)

    fun canManageDuas(role: AdminRole): Boolean {
        return role == AdminRole.SUPER_ADMIN || role == AdminRole.ADMIN || role == AdminRole.IMAM || role == AdminRole.EDITOR
    }

    fun canManageCommittee(user: AdminUser?): Boolean =
        user != null && user.isActive && canManageCommittee(user.role)

    fun canManageCommittee(role: AdminRole): Boolean {
        return role == AdminRole.SUPER_ADMIN || role == AdminRole.ADMIN
    }

    fun canManageFatwas(user: AdminUser?): Boolean =
        user != null && user.isActive && canManageFatwas(user.role)

    fun canManageFatwas(role: AdminRole): Boolean {
        return role == AdminRole.SUPER_ADMIN || role == AdminRole.ADMIN || role == AdminRole.IMAM
    }

    fun canManageDonations(user: AdminUser?): Boolean =
        user != null && user.isActive && canManageDonations(user.role)

    fun canManageDonations(role: AdminRole): Boolean {
        return role == AdminRole.SUPER_ADMIN || role == AdminRole.ADMIN
    }

    fun canManageAdmins(user: AdminUser?): Boolean =
        user != null && user.isActive && canManageAdmins(user.role)

    fun canManageAdmins(role: AdminRole): Boolean {
        return role == AdminRole.SUPER_ADMIN
    }

    fun canViewActivityLogs(user: AdminUser?): Boolean =
        user != null && user.isActive && canViewActivityLogs(user.role)

    fun canViewActivityLogs(role: AdminRole): Boolean {
        return role == AdminRole.SUPER_ADMIN || role == AdminRole.ADMIN
    }

    fun canPublishImmediately(user: AdminUser?): Boolean =
        user != null && user.isActive && canPublishImmediately(user.role)

    fun canPublishImmediately(role: AdminRole): Boolean {
        return role == AdminRole.SUPER_ADMIN || role == AdminRole.ADMIN || role == AdminRole.IMAM
    }
}
