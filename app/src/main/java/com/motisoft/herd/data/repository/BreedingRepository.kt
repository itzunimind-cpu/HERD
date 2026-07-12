package com.motisoft.herd.data.repository

import com.motisoft.herd.data.local.dao.BreedingDao
import com.motisoft.herd.data.local.dao.CowDao
import com.motisoft.herd.data.local.entity.BreedingInfoEntity
import com.motisoft.herd.data.local.entity.CalvingHistoryEntity
import com.motisoft.herd.data.remote.dto.BreedingInfoDto
import com.motisoft.herd.data.remote.dto.CalvingHistoryDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BreedingRepository @Inject constructor(
    private val breedingDao: BreedingDao,
    private val cowDao: CowDao,
    private val supabase: SupabaseClient,
    private val authRepository: AuthRepository,
) {
    fun observeInfo(cowTag: String): Flow<BreedingInfoEntity?> = breedingDao.observeInfo(cowTag)

    fun observeCalvingHistory(cowTag: String): Flow<List<CalvingHistoryEntity>> =
        breedingDao.observeCalvingHistory(cowTag)

    suspend fun saveInfo(info: BreedingInfoEntity) {
        val dirty = info.copy(dirty = true, updatedAt = Clock.System.now())
        breedingDao.upsertInfo(dirty)
        pushInfo(dirty)
    }

    suspend fun addCalvingEntry(entry: CalvingHistoryEntity) {
        val dirty = entry.copy(dirty = true, updatedAt = Clock.System.now())
        val id = breedingDao.upsertCalvingEntry(dirty)
        pushCalvingEntry(dirty.copy(id = id))
    }

    suspend fun syncDirty() {
        breedingDao.getDirtyInfo().forEach { pushInfo(it) }
        breedingDao.getDirtyCalvingHistory().forEach { pushCalvingEntry(it) }
    }

    private suspend fun pushInfo(info: BreedingInfoEntity) {
        val ownerId = authRepository.currentUserId ?: return
        val cowId = cowDao.getByTag(info.cowTag)?.remoteId ?: return
        runCatching {
            val dto = BreedingInfoDto(
                id = info.remoteId,
                ownerId = ownerId,
                cowId = cowId,
                pregnancyStatus = info.pregnancyStatus,
                lastHeatDate = info.lastHeatDate,
                inseminationDate = info.inseminationDate,
                expectedCalvingDate = info.expectedCalvingDate,
            )
            val result = supabase.from("breeding_info")
                .upsert(dto) { onConflict = "cow_id"; select() }
                .decodeSingle<BreedingInfoDto>()
            breedingDao.upsertInfo(info.copy(remoteId = result.id, dirty = false))
        }
    }

    private suspend fun pushCalvingEntry(entry: CalvingHistoryEntity) {
        val ownerId = authRepository.currentUserId ?: return
        val cowId = cowDao.getByTag(entry.cowTag)?.remoteId ?: return
        runCatching {
            val dto = CalvingHistoryDto(
                id = entry.remoteId,
                ownerId = ownerId,
                cowId = cowId,
                calfSex = entry.calfSex,
                calvingDate = entry.calvingDate,
            )
            val result = supabase.from("calving_history")
                .upsert(dto) { select() }
                .decodeSingle<CalvingHistoryDto>()
            breedingDao.upsertCalvingEntry(entry.copy(remoteId = result.id, dirty = false))
        }
    }
}
