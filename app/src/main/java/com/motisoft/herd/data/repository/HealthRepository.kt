package com.motisoft.herd.data.repository

import com.motisoft.herd.data.local.dao.CowDao
import com.motisoft.herd.data.local.dao.HealthDao
import com.motisoft.herd.data.local.entity.HealthStatusEntity
import com.motisoft.herd.data.local.entity.IllnessLogEntity
import com.motisoft.herd.data.local.entity.VaccinationEntity
import com.motisoft.herd.data.remote.dto.HealthStatusDto
import com.motisoft.herd.data.remote.dto.IllnessLogDto
import com.motisoft.herd.data.remote.dto.VaccinationDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthRepository @Inject constructor(
    private val healthDao: HealthDao,
    private val cowDao: CowDao,
    private val supabase: SupabaseClient,
    private val authRepository: AuthRepository,
) {
    fun observeStatus(cowTag: String): Flow<HealthStatusEntity?> = healthDao.observeStatus(cowTag)
    fun observeVaccinations(cowTag: String): Flow<List<VaccinationEntity>> = healthDao.observeVaccinations(cowTag)
    fun observeIllnessLog(cowTag: String): Flow<List<IllnessLogEntity>> = healthDao.observeIllnessLog(cowTag)

    suspend fun saveStatus(status: HealthStatusEntity) {
        val dirty = status.copy(dirty = true, updatedAt = Clock.System.now())
        healthDao.upsertStatus(dirty)
        pushStatus(dirty)
    }

    suspend fun addVaccination(vaccination: VaccinationEntity) {
        val dirty = vaccination.copy(dirty = true, updatedAt = Clock.System.now())
        healthDao.upsertVaccination(dirty)
        pushVaccination(dirty)
    }

    suspend fun addIllnessEntry(entry: IllnessLogEntity) {
        val dirty = entry.copy(dirty = true, updatedAt = Clock.System.now())
        healthDao.upsertIllnessEntry(dirty)
        pushIllnessEntry(dirty)
    }

    suspend fun syncDirty() {
        healthDao.getDirtyStatus().forEach { pushStatus(it) }
        healthDao.getDirtyVaccinations().forEach { pushVaccination(it) }
        healthDao.getDirtyIllnessLog().forEach { pushIllnessEntry(it) }
    }

    private suspend fun pushStatus(status: HealthStatusEntity) {
        val ownerId = authRepository.currentUserId ?: return
        val cowId = cowDao.getByTag(status.cowTag)?.remoteId ?: return
        runCatching {
            val dto = HealthStatusDto(
                id = status.remoteId,
                ownerId = ownerId,
                cowId = cowId,
                currentStatus = status.currentStatus,
            )
            val result = supabase.from("health_status")
                .upsert(dto) { onConflict = "cow_id"; select() }
                .decodeSingle<HealthStatusDto>()
            healthDao.upsertStatus(status.copy(remoteId = result.id, dirty = false))
        }
    }

    private suspend fun pushVaccination(vaccination: VaccinationEntity) {
        val ownerId = authRepository.currentUserId ?: return
        val cowId = cowDao.getByTag(vaccination.cowTag)?.remoteId ?: return
        runCatching {
            val dto = VaccinationDto(
                id = vaccination.remoteId,
                ownerId = ownerId,
                cowId = cowId,
                vaccineName = vaccination.vaccineName,
                date = vaccination.date,
            )
            val result = supabase.from("vaccinations")
                .upsert(dto) { select() }
                .decodeSingle<VaccinationDto>()
            healthDao.upsertVaccination(vaccination.copy(remoteId = result.id, dirty = false))
        }
    }

    private suspend fun pushIllnessEntry(entry: IllnessLogEntity) {
        val ownerId = authRepository.currentUserId ?: return
        val cowId = cowDao.getByTag(entry.cowTag)?.remoteId ?: return
        runCatching {
            val dto = IllnessLogDto(
                id = entry.remoteId,
                ownerId = ownerId,
                cowId = cowId,
                description = entry.description,
                date = entry.date,
                treatment = entry.treatment,
            )
            val result = supabase.from("illness_log")
                .upsert(dto) { select() }
                .decodeSingle<IllnessLogDto>()
            healthDao.upsertIllnessEntry(entry.copy(remoteId = result.id, dirty = false))
        }
    }
}
