package com.example.data.model

enum class FatwaCategory(val titleBn: String, val iconKey: String) {
    ALL("সকল প্রশ্ন", "all"),
    SALAT("নামাজ ও জামাত", "salat"),
    TAHARAT("পবিত্রতা ও ওজু", "taharat"),
    SAWM("রোজা ও রমজান", "sawm"),
    ZAKAT("জাকাত ও সাদাকা", "zakat"),
    MARRIAGE_FAMILY("পরিবার ও বিবাহ", "family"),
    DAILY_LIFE("দৈনন্দিন জীবন ও ব্যবসা", "lifestyle"),
    MISCELLANEOUS("বিবিধ মাসআলা", "misc")
}

data class FatwaArticle(
    val id: String,
    val questionBn: String,
    val answerBn: String,
    val category: FatwaCategory,
    val answeredBy: String = "মুফতি মাওলানা আব্দুল ওয়াদুদ (খতিব, বায়তুল আমান জামে মসজিদ)",
    val referenceBn: String = "সহীহ বুখারী, ফতোয়ায়ে শামী",
    val dateBn: String = "২০২৫",
    val isBookmarked: Boolean = false
)

data class UserQuestionSubmission(
    val id: String,
    val senderName: String,
    val senderPhone: String,
    val category: FatwaCategory,
    val questionText: String,
    val isPrivate: Boolean = false,
    val submissionDate: Long = System.currentTimeMillis(),
    val status: String = "ইমাম সাহেবের পর্যালোচনাধীন",
    val replyText: String = "",
    val repliedBy: String = "",
    val replyDateBn: String = ""
)
