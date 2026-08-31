package com.robiul.mosquetime.data.model

enum class DonationFundType(val titleBn: String, val subtitleBn: String) {
    GENERAL("সাধারণ মসজিদ তহবিল", "মসজিদের নিত্যদিনের পরিচালন ব্যয়"),
    CONSTRUCTION("নির্মাণ ও সংস্কার তহবিল", "মসজিদ সম্প্রসারণ ও আধুনিকায়ন"),
    IMAM_WELFARE("ইমাম-মুয়াজ্জিন কল্যাণ", "সম্মানী ও শিক্ষক সহায়তা তহবিল"),
    ORPHAN_POOR("দরিদ্র ও এতিম সহায়তা", "এলাকার অসহায় ও এতিমদের সহযোগিতা"),
    RAMADAN("রমজান ইফতার ও ঈদ তহবিল", "রোজাদারদের ইফতার ও ঈদ সামগ্রী বিতরণ")
}

data class BankAccountInfo(
    val bankName: String,
    val accountName: String,
    val accountNumber: String,
    val branchName: String,
    val routingNumber: String
)

data class MobileAccountInfo(
    val provider: String, // bKash, Nagad, Rocket
    val number: String,
    val type: String // Merchant / Personal
)

data class DonationRecord(
    val id: String,
    val fundTitle: String,
    val amount: Long,
    val paymentMethod: String,
    val transactionId: String,
    val donorName: String,
    val donorPhone: String,
    val dateString: String,
    val status: String = "গৃহীত হয়েছে"
)
