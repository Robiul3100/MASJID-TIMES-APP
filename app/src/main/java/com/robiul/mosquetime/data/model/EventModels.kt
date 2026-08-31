package com.robiul.mosquetime.data.model

enum class EventCategory(val titleBn: String) {
    ALL("সকল অনুষ্ঠান"),
    WAZ("ওয়াজ মাহফিল"),
    HALQA("তাফসীর ও হালকাহ"),
    QURAN_CLASS("কুরআন শিক্ষা"),
    RAMADAN("রমজান ও ইফতার"),
    SPECIAL_DUA("বিশেষ দোয়ার মাহফিল")
}

data class MosqueEvent(
    val id: String,
    val title: String,
    val dateBn: String,
    val timeBn: String,
    val locationBn: String,
    val description: String,
    val category: EventCategory,
    val speaker: String,
    val isUpcoming: Boolean = true,
    val hasReminderSet: Boolean = false
)
