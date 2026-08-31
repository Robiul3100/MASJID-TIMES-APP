package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.FatwaCategory
import com.example.data.model.UserQuestionSubmission

@Entity(tableName = "user_questions")
data class UserQuestionEntity(
    @PrimaryKey val id: String,
    val senderName: String,
    val senderPhone: String,
    val categoryName: String,
    val questionText: String,
    val isPrivate: Boolean = false,
    val submissionDate: Long = System.currentTimeMillis(),
    val status: String = "ইমাম সাহেবের পর্যালোচনাধীন",
    val replyText: String = "",
    val repliedBy: String = "",
    val replyDateBn: String = ""
) {
    fun toDomainModel(): UserQuestionSubmission {
        val cat = try {
            FatwaCategory.valueOf(categoryName)
        } catch (e: Exception) {
            FatwaCategory.MISCELLANEOUS
        }
        return UserQuestionSubmission(
            id = id,
            senderName = senderName,
            senderPhone = senderPhone,
            category = cat,
            questionText = questionText,
            isPrivate = isPrivate,
            submissionDate = submissionDate,
            status = status,
            replyText = replyText,
            repliedBy = repliedBy,
            replyDateBn = replyDateBn
        )
    }

    companion object {
        fun fromDomainModel(model: UserQuestionSubmission): UserQuestionEntity {
            return UserQuestionEntity(
                id = model.id,
                senderName = model.senderName,
                senderPhone = model.senderPhone,
                categoryName = model.category.name,
                questionText = model.questionText,
                isPrivate = model.isPrivate,
                submissionDate = model.submissionDate,
                status = model.status,
                replyText = model.replyText,
                repliedBy = model.repliedBy,
                replyDateBn = model.replyDateBn
            )
        }
    }
}
