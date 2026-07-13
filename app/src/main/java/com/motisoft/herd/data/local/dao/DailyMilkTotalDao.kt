package com.motisoft.herd.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.motisoft.herd.data.local.entity.DailyMilkTotalEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface DailyMilkTotalDao {
    @Query("SELECT * FROM daily_milk_totals WHERE ownerId = :ownerId AND date = :date")
    fun observeByDate(ownerId: String, date: LocalDate): Flow<DailyMilkTotalEntity?>

    @Query("SELECT * FROM daily_milk_totals WHERE ownerId = :ownerId AND date = :date")
    suspend fun getByDate(ownerId: String, date: LocalDate): DailyMilkTotalEntity?

    @Query("SELECT * FROM daily_milk_totals WHERE dirty = 1")
    suspend fun getDirty(): List<DailyMilkTotalEntity>

    @Upsert
    suspend fun upsert(total: DailyMilkTotalEntity)
}
