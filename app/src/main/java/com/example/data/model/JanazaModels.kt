package com.example.data.model

data class JanazaNotice(
    val id: String,
    val deceasedNameBn: String,
    val deceasedAge: String,
    val residenceBn: String,
    val demiseTimeBn: String,
    val janazaTimeBn: String,
    val janazaLocationBn: String,
    val imamNameBn: String,
    val graveyardBn: String,
    val contactFamilyPhone: String,
    val specialMessageBn: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class EmergencyAlert(
    val id: String,
    val titleBn: String,
    val categoryBn: String, // রক্তদান, হারানো বিজ্ঞপ্তি, জরুরি মেরামত, প্রাকৃতিক দুর্যোগ
    val descriptionBn: String,
    val urgencyLevel: String, // HIGH, MEDIUM, NORMAL
    val contactPerson: String,
    val contactPhone: String,
    val dateBn: String,
    val isResolved: Boolean = false
)
