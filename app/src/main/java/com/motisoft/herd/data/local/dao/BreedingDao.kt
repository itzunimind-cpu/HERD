package com.motisoft.herd.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.motisoft.herd.data.local.entity.BreedingInfoEntity
import com.motisoft.herd.data.local.entity.CalvingHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedingDao {
    @Query("SELECT * FROM breeding_info WHERE cowTag = :cowTag")
    fun observeInfo(cowTag: String): Flow<BreedingInfoEntity?>

    @Query("SELECT * FROM breeding_info WHERE dirty = 1")
    suspend fun getDirtyInfo(): List<BreedingInfoEntity>

    @Upsert
    suspend fun upsertInfo(info: BreedingInfoEntity)

    @Query("SELECT * FROM calving_history WHERE cowTag = :cowTag ORDER BY calvingDate DESC")
    fun observeCalvingHistory(cowTag: String): Flow<List<CalvingHistoryEntity>>

    @Query("SELECT * FROM calving_history WHERE dirty = 1")
    suspend fun getDirtyCalvingHistory(): List<CalvingHistoryEntity>

    @Upsert
    suspend fun upsertCalvingEntry(entry: CalvingHistoryEntity): Long
}
