package com.robiul.mosquetime.data.model

data class QuranVerse(
    val verseNumber: Int,
    val arabicText: String,
    val bengaliTranslation: String,
    val bengaliPronunciation: String = ""
)

data class QuranSurah(
    val number: Int,
    val nameArabic: String,
    val nameBengali: String,
    val nameEnglish: String,
    val meaningBengali: String,
    val totalVerses: Int,
    val revelationType: String, // Makki or Madani
    val verses: List<QuranVerse> = emptyList()
)

data class QuranBookmark(
    val surahNumber: Int,
    val surahName: String,
    val verseNumber: Int,
    val timestamp: Long = System.currentTimeMillis()
)
