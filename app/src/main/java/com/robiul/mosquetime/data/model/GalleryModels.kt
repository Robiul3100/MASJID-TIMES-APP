package com.robiul.mosquetime.data.model

enum class GalleryCategory(val titleBn: String) {
    ALL("সকল ছবি"),
    ARCHITECTURE("মসজিদের রূপ"),
    JUMAH("জুমার জামাত"),
    EVENTS("ইসলামিক আয়োজন"),
    RAMADAN("মাহে রমজান"),
    CONSTRUCTION("উন্নয়ন কাজ")
}

data class GalleryItem(
    val id: String,
    val title: String,
    val description: String,
    val category: GalleryCategory,
    val imageUrl: String,
    val date: String
)
