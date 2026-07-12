package com.motisoft.herd.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.motisoft.herd.data.local.entity.MilkRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MilkDao {
    @Query("SELECT * FROM milk_records WHERE cowTag = :cowTag ORDER BY date DESC")
    fun observeRecords(cowTag: String): Flow<List<MilkRecordEntity>>

    @Query("SELECT * FROM milk_records WHERE dirty = 1")
    suspend fun getDirty(): List<MilkRecordEntity>

    @Upsert
    suspend fun upsert(record: MilkRecordEntity)
}
