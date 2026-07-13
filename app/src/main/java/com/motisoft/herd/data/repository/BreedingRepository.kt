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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
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

    // A cow is "due" every 21 days (the bovine estrus cycle length) counted from
    // whichever is more recent: her last recorded heat, or her last calving (heat
    // resumes some time after giving birth). Stops once a positive pregnancy test
    // is recorded, since a confirmed-pregnant cow isn't cycling.
    suspend fun findCowsDueForHeatCheck(today: LocalDate): List<Pair<String, String>> {
        val due = mutableListOf<Pair<String, String>>()
        breedingDao.getAllInfo().forEach { info ->
            val confirmedPregnant = info.pregnancyStatus == "गाभण" && info.pregnancyTestDate != null
            if (confirmedPregnant) return@forEach

            val latestCalving = breedingDao.getLatestCalvingDate(info.cowTag)
            val anchor = listOfNotNull(info.lastHeatDate, latestCalving).maxOrNull() ?: return@forEach

            val daysSince = anchor.daysUntil(today)
            if (daysSince > 0 && daysSince % 21 == 0) {
                val name = cowDao.getByTag(info.cowTag)?.name ?: info.cowTag
                due.add(info.cowTag to name)
            }
        }
        return due
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
                pregnancyTestDate = info.pregnancyTestDate,
                expectedCalvingDate = info.expectedCalvingDate,
                semenBreed = info.semenBreed,
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
