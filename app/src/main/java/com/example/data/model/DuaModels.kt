package com.example.data.model

enum class DuaCategory(val titleBn: String, val iconName: String) {
    ALL("সকল দোয়া", "all"),
    DAILY("দৈনন্দিন দোয়া", "daily"),
    MORNING_EVENING("সকাল-সন্ধ্যার যিকির", "morning"),
    PRAYER("নামাজের দোয়া", "prayer"),
    FOOD("খাবারের দোয়া", "food"),
    SLEEP("ঘুমের দোয়া", "sleep"),
    TRAVEL("সফরের দোয়া", "travel"),
    MOSQUE("মসজিদের দোয়া", "mosque"),
    PROTECTION("সুরক্ষা ও সুস্থতা", "protection"),
    FORGIVENESS("ক্ষমা প্রার্থনা", "forgiveness")
}

data class DuaItem(
    val id: String,
    val titleBn: String,
    val category: DuaCategory,
    val arabicText: String,
    val pronunciationBn: String,
    val meaningBn: String,
    val reference: String,
    val benefit: String = "",
    val repetitionCount: Int = 1,
    val isBookmarked: Boolean = false
)

data class TasbeehItem(
    val id: String,
    val nameBn: String,
    val arabicText: String,
    val targetCount: Int = 33,
    var currentCount: Int = 0
)
