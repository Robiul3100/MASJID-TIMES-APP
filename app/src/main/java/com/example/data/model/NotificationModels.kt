package com.example.data.model

enum class NotificationCategory(val titleBn: String) {
    ALL("সকল"),
    PRAYER("নামাজের সময়"),
    JUMAH("জুমার নোটিশ"),
    NOTICE("নোটিশ আপডেট"),
    EVENT("অনুষ্ঠান"),
    SPECIAL("বিশেষ ঘোষণা")
}

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val timeAgo: String,
    val category: NotificationCategory,
    val isRead: Boolean = false,
    val targetRoute: String? = null
)
