package com.robiul.mosquetime.model

data class PrayerTimeItem(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val adhanTime: String,
    val iqamahTime: String,
    val isAlarmEnabled: Boolean = true,
    val isNext: Boolean = false,
    val isPassed: Boolean = false
)

data class District(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val latitude: Double,
    val longitude: Double,
    val fajrOffsetMin: Int,
    val dhuhrOffsetMin: Int,
    val asrOffsetMin: Int,
    val maghribOffsetMin: Int,
    val ishaOffsetMin: Int
)

data class MosqueInfo(
    val name: String = "চৌধুরী পাটোয়ারী বাড়ি জামে মসজিদ",
    val location: String = "পাটোয়ারী বাড়ি, চাঁদপুর / ঢাকা",
    val establishedYear: String = "১৯৮৫",
    val imamName: String = "মাওলানা মুহাম্মাদ আব্দুল করিম",
    val muazzinName: String = "হাফেজ মো: ইব্রাহিম খলিল",
    val presidentName: String = "হাজী মো: রফিকুল ইসলাম চৌধুরী"
)

data class NoticeItem(
    val id: String,
    val title: String,
    val description: String,
    val dateBn: String,
    val category: String,
    val isPinned: Boolean = false
)

data class HujurKhanaItem(
    val id: String,
    val dayBn: String,
    val dateBn: String,
    val hostNameBn: String,
    val houseAddressBn: String,
    val contactPhone: String,
    val mealType: String = "দুপুর ও রাত",
    val isToday: Boolean = false
)

data class DuaItem(
    val id: String,
    val titleBn: String,
    val arabicText: String,
    val pronunciationBn: String,
    val translationBn: String,
    val reference: String,
    val category: String
)
