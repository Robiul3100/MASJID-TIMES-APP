package com.robiul.mosquetime.data.model

data class RamadanDay(
    val ramadanDayNumber: Int,
    val dateBengali: String,
    val dateEnglish: String,
    val dayName: String,
    val sehriEndTime: String,
    val fajrAzanTime: String,
    val iftarTime: String,
    val isToday: Boolean = false,
    val isFastCompleted: Boolean = false
)

data class RamadanDua(
    val titleBn: String,
    val arabicText: String,
    val transliterationBn: String,
    val meaningBn: String,
    val occasionBn: String
)
