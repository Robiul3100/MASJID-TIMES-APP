package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.TasbihRecord

@Entity(tableName = "tasbih_records")
data class TasbihRecordEntity(
    @PrimaryKey val id: String,
    val dhikrId: String,
    val dhikrNameBn: String,
    val count: Int,
    val target: Int,
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): TasbihRecord {
        return TasbihRecord(
            id = id,
            dhikrId = dhikrId,
            dhikrNameBn = dhikrNameBn,
            count = count,
            target = target,
            dateString = dateString,
            timestamp = timestamp
        )
    }

    companion object {
        fun fromDomainModel(record: TasbihRecord): TasbihRecordEntity {
            return TasbihRecordEntity(
                id = record.id,
                dhikrId = record.dhikrId,
                dhikrNameBn = record.dhikrNameBn,
                count = record.count,
                target = record.target,
                dateString = record.dateString,
                timestamp = record.timestamp
            )
        }
    }
}
