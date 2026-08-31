package com.robiul.mosquetime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.robiul.mosquetime.data.model.District

@Entity(tableName = "user_locations")
data class UserLocationEntity(
    @PrimaryKey val id: String,
    val districtId: String,
    val districtNameBn: String,
    val districtNameEn: String,
    val divisionNameBn: String = "ঢাকা",
    val latitude: Double = 23.8103,
    val longitude: Double = 90.4125,
    val fajrOffsetMinutes: Int = 0,
    val dhuhrOffsetMinutes: Int = 0,
    val asrOffsetMinutes: Int = 0,
    val maghribOffsetMinutes: Int = 0,
    val ishaOffsetMinutes: Int = 0,
    val isCurrentSelected: Boolean = false,
    val isGpsDerived: Boolean = false,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
) {
    fun toDistrict(): District {
        return District(
            id = districtId,
            nameBn = districtNameBn,
            nameEn = districtNameEn,
            fajrOffsetMinutes = fajrOffsetMinutes,
            dhuhrOffsetMinutes = dhuhrOffsetMinutes,
            asrOffsetMinutes = asrOffsetMinutes,
            maghribOffsetMinutes = maghribOffsetMinutes,
            ishaOffsetMinutes = ishaOffsetMinutes
        )
    }

    companion object {
        fun fromDistrict(district: District, isSelected: Boolean = false, isGps: Boolean = false): UserLocationEntity {
            return UserLocationEntity(
                id = district.id,
                districtId = district.id,
                districtNameBn = district.nameBn,
                districtNameEn = district.nameEn,
                fajrOffsetMinutes = district.fajrOffsetMinutes,
                dhuhrOffsetMinutes = district.dhuhrOffsetMinutes,
                asrOffsetMinutes = district.asrOffsetMinutes,
                maghribOffsetMinutes = district.maghribOffsetMinutes,
                ishaOffsetMinutes = district.ishaOffsetMinutes,
                isCurrentSelected = isSelected,
                isGpsDerived = isGps,
                lastUpdatedTimestamp = System.currentTimeMillis()
            )
        }
    }
}
