package com.example.data.model

enum class NoticeCategory(val titleBn: String) {
    ALL("সকল"),
    GENERAL("সাধারণ নোটিশ"),
    JUMAH("জুমার ঘোষণা"),
    EVENT("ইসলামিক অনুষ্ঠান"),
    SPECIAL("বিশেষ ঘোষণা"),
    URGENT("জরুরি নোটিশ")
}

data class NoticeItem(
    val id: String,
    val title: String,
    val summary: String,
    val fullContent: String,
    val category: NoticeCategory,
    val publishedDate: String,
    val isPinned: Boolean = false,
    val author: String = "মসজিদ পরিচালনা কমিটি",
    val attachmentUrl: String? = null
)
