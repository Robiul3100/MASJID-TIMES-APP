package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.UserQuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserQuestionDao {
    @Query("SELECT * FROM user_questions ORDER BY submissionDate DESC")
    fun getAllUserQuestionsFlow(): Flow<List<UserQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserQuestion(question: UserQuestionEntity)

    @Query("UPDATE user_questions SET replyText = :replyText, replyDateBn = :replyDateBn, repliedBy = :repliedBy, status = 'উত্তর সম্পন্ন' WHERE id = :id")
    suspend fun updateQuestionReply(id: String, replyText: String, replyDateBn: String, repliedBy: String)

    @Query("SELECT * FROM user_questions WHERE id = :id LIMIT 1")
    suspend fun getQuestionById(id: String): UserQuestionEntity?

    @Query("DELETE FROM user_questions WHERE id = :id")
    suspend fun deleteUserQuestion(id: String)
}
