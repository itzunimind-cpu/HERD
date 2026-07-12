package com.motisoft.herd.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.motisoft.herd.data.local.entity.DailyLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyLogDao {
    @Query("SELECT * FROM daily_logs WHERE cowTag = :cowTag ORDER BY date DESC")
    fun observeLogs(cowTag: String): Flow<List<DailyLogEntity>>

    @Query("SELECT * FROM daily_logs WHERE dirty = 1")
    suspend fun getDirty(): List<DailyLogEntity>

    @Upsert
    suspend fun upsert(log: DailyLogEntity)
}
