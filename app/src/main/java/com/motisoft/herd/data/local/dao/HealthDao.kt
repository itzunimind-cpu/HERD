package com.motisoft.herd.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.motisoft.herd.data.local.entity.HealthStatusEntity
import com.motisoft.herd.data.local.entity.IllnessLogEntity
import com.motisoft.herd.data.local.entity.VaccinationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {
    @Query("SELECT * FROM health_status WHERE cowTag = :cowTag")
    fun observeStatus(cowTag: String): Flow<HealthStatusEntity?>

    @Query("SELECT * FROM health_status WHERE dirty = 1")
    suspend fun getDirtyStatus(): List<HealthStatusEntity>

    @Upsert
    suspend fun upsertStatus(status: HealthStatusEntity)

    @Query("SELECT * FROM vaccinations WHERE cowTag = :cowTag ORDER BY date DESC")
    fun observeVaccinations(cowTag: String): Flow<List<VaccinationEntity>>

    @Query("SELECT * FROM vaccinations WHERE dirty = 1")
    suspend fun getDirtyVaccinations(): List<VaccinationEntity>

    @Upsert
    suspend fun upsertVaccination(vaccination: VaccinationEntity)

    @Query("SELECT * FROM illness_log WHERE cowTag = :cowTag ORDER BY date DESC")
    fun observeIllnessLog(cowTag: String): Flow<List<IllnessLogEntity>>

    @Query("SELECT * FROM illness_log WHERE dirty = 1")
    suspend fun getDirtyIllnessLog(): List<IllnessLogEntity>

    @Upsert
    suspend fun upsertIllnessEntry(entry: IllnessLogEntity)
}
