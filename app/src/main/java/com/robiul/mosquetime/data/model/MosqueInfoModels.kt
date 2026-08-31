package com.robiul.mosquetime.data.model

data class FacilityItem(
    val title: String,
    val description: String,
    val iconType: String
)

data class MosqueDetails(
    val nameBn: String,
    val nameEn: String,
    val establishedYear: String,
    val address: String,
    val district: String,
    val capacity: String,
    val floors: String,
    val history: String,
    val description: String,
    val imamName: String,
    val imamTitle: String,
    val imamEducation: String,
    val imamPhone: String,
    val muazzinName: String,
    val muazzinPhone: String,
    val khademName: String,
    val officePhone: String,
    val officeEmail: String,
    val website: String,
    val facilities: List<FacilityItem>
)

enum class CommitteeCategory(val titleBn: String) {
    ALL("সকল সদস্য"),
    EXECUTIVE("কার্যনির্বাহী পরিষদ"),
    OFFICE_BEARERS("মূল কর্মকর্তা"),
    ADVISORY("উপদেষ্টা পরিষদ"),
    GENERAL_MEMBERS("সাধারণ সদস্য")
}

data class CommitteeMember(
    val id: String,
    val name: String,
    val designationBn: String,
    val category: CommitteeCategory,
    val phone: String,
    val profession: String = "",
    val termYears: String = "২০২৪-২০২৬",
    val photoRes: String? = null
)
