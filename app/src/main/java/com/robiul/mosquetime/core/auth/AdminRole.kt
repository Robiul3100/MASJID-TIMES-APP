package com.robiul.mosquetime.core.auth

enum class AdminRole(val displayNameBn: String, val level: Int) {
    SUPER_ADMIN("সুপার অ্যাডমিন", 1),
    ADMIN("মসজিদ অ্যাডমিন", 2),
    IMAM("খতিব / ইমাম সাহেব", 3),
    EDITOR("সম্পাদক / প্রচার সম্পাদক", 4),
    VIEWER("পর্যবেক্ষক", 5);

    companion object {
        fun fromString(roleStr: String?): AdminRole {
            return when (roleStr?.uppercase()) {
                "SUPER_ADMIN" -> SUPER_ADMIN
                "ADMIN" -> ADMIN
                "IMAM" -> IMAM
                "EDITOR" -> EDITOR
                "VIEWER" -> VIEWER
                else -> VIEWER
            }
        }
    }
}

data class AdminUser(
    val uid: String = "",
    val email: String = "",
    val nameBn: String = "",
    val designation: String = "",
    val role: AdminRole = AdminRole.VIEWER,
    val phone: String = "",
    val isActive: Boolean = true,
    val lastLoginMillis: Long = System.currentTimeMillis()
)
