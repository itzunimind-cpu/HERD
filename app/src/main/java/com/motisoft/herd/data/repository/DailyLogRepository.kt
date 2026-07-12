package com.motisoft.herd.data.repository

import com.motisoft.herd.data.local.dao.CowDao
import com.motisoft.herd.data.local.dao.DailyLogDao
import com.motisoft.herd.data.local.entity.DailyLogEntity
import com.motisoft.herd.data.remote.dto.DailyLogDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyLogRepository @Inject constructor(
    private val dailyLogDao: DailyLogDao,
    private val cowDao: CowDao,
    private val supabase: SupabaseClient,
    private val authRepository: AuthRepository,
) {
    fun observeLogs(cowTag: String): Flow<List<DailyLogEntity>> = dailyLogDao.observeLogs(cowTag)

    suspend fun saveLog(log: DailyLogEntity) {
        val dirty = log.copy(dirty = true, updatedAt = Clock.System.now())
        dailyLogDao.upsert(dirty)
        pushLog(dirty)
    }

    suspend fun syncDirty() {
        dailyLogDao.getDirty().forEach { pushLog(it) }
    }

    private suspend fun pushLog(log: DailyLogEntity) {
        val ownerId = authRepository.currentUserId ?: return
        val cowId = cowDao.getByTag(log.cowTag)?.remoteId ?: return
        runCatching {
            val dto = DailyLogDto(
                id = log.remoteId,
                ownerId = ownerId,
                cowId = cowId,
                date = log.date,
                feed = log.feed,
                water = log.water,
                temperature = log.temperature,
                activityNotes = log.activityNotes,
            )
            val result = supabase.from("daily_logs")
                .upsert(dto) { onConflict = "cow_id,date"; select() }
                .decodeSingle<DailyLogDto>()
            dailyLogDao.upsert(log.copy(remoteId = result.id, dirty = false))
        }
    }
}
