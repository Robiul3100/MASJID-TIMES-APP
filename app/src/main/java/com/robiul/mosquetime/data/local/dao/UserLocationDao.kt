package com.robiul.mosquetime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.robiul.mosquetime.data.local.entity.UserLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserLocationDao {

    @Query("SELECT * FROM user_locations ORDER BY isCurrentSelected DESC, districtNameBn ASC")
    fun getAllLocationsFlow(): Flow<List<UserLocationEntity>>

    @Query("SELECT * FROM user_locations WHERE isCurrentSelected = 1 LIMIT 1")
    fun getSelectedLocationFlow(): Flow<UserLocationEntity?>

    @Query("SELECT * FROM user_locations WHERE isCurrentSelected = 1 LIMIT 1")
    suspend fun getSelectedLocation(): UserLocationEntity?

    @Query("SELECT * FROM user_locations WHERE districtId = :districtId LIMIT 1")
    suspend fun getLocationById(districtId: String): UserLocationEntity?

    @Query("SELECT COUNT(*) FROM user_locations")
    suspend fun getLocationCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<UserLocationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLocation(location: UserLocationEntity)

    @Query("UPDATE user_locations SET isCurrentSelected = 0")
    suspend fun clearAllSelectedFlags()

    @Query("UPDATE user_locations SET isCurrentSelected = 1, lastUpdatedTimestamp = :timestamp WHERE districtId = :districtId")
    suspend fun markLocationAsSelected(districtId: String, timestamp: Long = System.currentTimeMillis())

    @Transaction
    suspend fun setSelectedLocation(districtId: String) {
        clearAllSelectedFlags()
        markLocationAsSelected(districtId)
    }

    @Query("DELETE FROM user_locations WHERE districtId = :districtId")
    suspend fun deleteLocation(districtId: String)

    @Query("DELETE FROM user_locations")
    suspend fun clearAllLocations()
}
