package com.example.data.model

enum class AuditActionCategory(val titleBn: String) {
    ALL("সকল কার্যক্রম"),
    PRAYER("নামাজের সময়"),
    MEALS("হুজুরের খানা"),
    NOTICES("নোটিশ বোর্ড"),
    EMERGENCY("জরুরি ও জানাজা"),
    NOTIFICATIONS("পুশ বার্তা"),
    EVENTS("ইভেন্ট ও মাহফিল"),
    COMMITTEE("কমিটি ও প্রশাসন"),
    DUAS("দোয়া ও কন্টেন্ট"),
    FATWAS("ফতোয়া ও প্রশ্নোত্তর"),
    DONATIONS("অনুদান ও হিসাব"),
    PROFILE("মসজিদ প্রোফাইল")
}

data class AdminAuditLog(
    val id: String,
    val adminNameBn: String,
    val adminRoleBn: String,
    val category: AuditActionCategory,
    val actionTitleBn: String,
    val detailsBn: String,
    val timestampBn: String,
    val isSystemAction: Boolean = false
)
