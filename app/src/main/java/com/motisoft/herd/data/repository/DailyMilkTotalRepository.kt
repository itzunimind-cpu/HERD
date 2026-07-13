package com.motisoft.herd.data.repository

import com.motisoft.herd.data.local.dao.DailyMilkTotalDao
import com.motisoft.herd.data.local.entity.DailyMilkTotalEntity
import com.motisoft.herd.data.remote.dto.DailyMilkTotalDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

// Same local-first, dirty-flag sync strategy as CowRepository/ProfileRepository,
// keyed by (ownerId, date) instead of a single row per user.
@Singleton
class DailyMilkTotalRepository @Inject constructor(
    private val dailyMilkTotalDao: DailyMilkTotalDao,
    private val supabase: SupabaseClient,
    private val authRepository: AuthRepository,
) {
    fun observeByDate(date: LocalDate): Flow<DailyMilkTotalEntity?> {
        val ownerId = authRepository.currentUserId ?: return flowOf(null)
        return dailyMilkTotalDao.observeByDate(ownerId, date)
    }

    suspend fun saveTotal(date: LocalDate, morningTotal: Double?, eveningTotal: Double?) {
        val ownerId = authRepository.currentUserId ?: return
        val dirty = DailyMilkTotalEntity(
            ownerId = ownerId,
            date = date,
            morningTotal = morningTotal,
            eveningTotal = eveningTotal,
            remoteId = null,
            updatedAt = Clock.System.now(),
            dirty = true,
        )
        dailyMilkTotalDao.upsert(dirty)
        pushTotal(dirty)
    }

    suspend fun syncDirty() {
        dailyMilkTotalDao.getDirty().forEach { pushTotal(it) }
    }

    private suspend fun pushTotal(total: DailyMilkTotalEntity) {
        runCatching {
            val dto = DailyMilkTotalDto(
                id = total.remoteId,
                ownerId = total.ownerId,
                date = total.date,
                morningTotal = total.morningTotal,
                eveningTotal = total.eveningTotal,
            )
            val result = supabase.from("daily_milk_totals")
                .upsert(dto) { onConflict = "owner_id,date"; select() }
                .decodeSingle<DailyMilkTotalDto>()
            dailyMilkTotalDao.upsert(total.copy(remoteId = result.id, dirty = false))
        }
    }
}
