package com.motisoft.herd.data.repository

import com.motisoft.herd.data.local.dao.CowDao
import com.motisoft.herd.data.local.dao.MilkDao
import com.motisoft.herd.data.local.entity.MilkRecordEntity
import com.motisoft.herd.data.remote.dto.MilkRecordDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MilkRepository @Inject constructor(
    private val milkDao: MilkDao,
    private val cowDao: CowDao,
    private val supabase: SupabaseClient,
    private val authRepository: AuthRepository,
) {
    fun observeRecords(cowTag: String): Flow<List<MilkRecordEntity>> = milkDao.observeRecords(cowTag)

    suspend fun saveRecord(record: MilkRecordEntity) {
        val dirty = record.copy(dirty = true, updatedAt = Clock.System.now())
        milkDao.upsert(dirty)
        pushRecord(dirty)
    }

    suspend fun syncDirty() {
        milkDao.getDirty().forEach { pushRecord(it) }
    }

    private suspend fun pushRecord(record: MilkRecordEntity) {
        val ownerId = authRepository.currentUserId ?: return
        val cowId = cowDao.getByTag(record.cowTag)?.remoteId ?: return
        runCatching {
            val dto = MilkRecordDto(
                id = record.remoteId,
                ownerId = ownerId,
                cowId = cowId,
                date = record.date,
                morningYield = record.morningYield,
                eveningYield = record.eveningYield,
                fatPct = record.fatPct,
                snfPct = record.snfPct,
            )
            val result = supabase.from("milk_records")
                .upsert(dto) { onConflict = "cow_id,date"; select() }
                .decodeSingle<MilkRecordDto>()
            milkDao.upsert(record.copy(remoteId = result.id, dirty = false))
        }
    }
}
