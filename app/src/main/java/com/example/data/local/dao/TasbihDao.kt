package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.TasbihRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TasbihDao {
    @Query("SELECT * FROM tasbih_records ORDER BY timestamp DESC")
    fun getAllRecordsFlow(): Flow<List<TasbihRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: TasbihRecordEntity)

    @Query("DELETE FROM tasbih_records WHERE id = :id")
    suspend fun deleteRecord(id: String)

    @Query("DELETE FROM tasbih_records")
    suspend fun clearAllRecords()
}
