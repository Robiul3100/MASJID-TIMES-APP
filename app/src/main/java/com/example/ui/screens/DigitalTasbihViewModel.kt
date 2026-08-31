package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.dao.TasbihDao
import com.example.data.local.entity.TasbihRecordEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DigitalTasbihViewModel @Inject constructor(
    private val tasbihDao: TasbihDao
) : ViewModel() {

    val savedRecords: StateFlow<List<TasbihRecordEntity>> = tasbihDao.getAllRecordsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveRecord(record: TasbihRecordEntity) {
        viewModelScope.launch {
            tasbihDao.insertRecord(record)
        }
    }

    fun deleteRecord(id: String) {
        viewModelScope.launch {
            tasbihDao.deleteRecord(id)
        }
    }

    fun clearAllRecords() {
        viewModelScope.launch {
            tasbihDao.clearAllRecords()
        }
    }
}
