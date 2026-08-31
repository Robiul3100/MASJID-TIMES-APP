package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.dao.UserQuestionDao
import com.example.data.local.entity.UserQuestionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AskImamViewModel @Inject constructor(
    private val userQuestionDao: UserQuestionDao
) : ViewModel() {

    val userQuestions: StateFlow<List<UserQuestionEntity>> = userQuestionDao.getAllUserQuestionsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun submitQuestion(question: UserQuestionEntity) {
        viewModelScope.launch {
            userQuestionDao.insertUserQuestion(question)
        }
    }
}
