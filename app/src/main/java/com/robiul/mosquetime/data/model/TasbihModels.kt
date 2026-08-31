package com.robiul.mosquetime.data.model

data class DhikrItem(
    val id: String,
    val arabicText: String,
    val transliterationBn: String,
    val meaningBn: String,
    val defaultTarget: Int = 33,
    val rewardBn: String = ""
)

data class TasbihRecord(
    val id: String,
    val dhikrId: String,
    val dhikrNameBn: String,
    val count: Int,
    val target: Int,
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis()
)
