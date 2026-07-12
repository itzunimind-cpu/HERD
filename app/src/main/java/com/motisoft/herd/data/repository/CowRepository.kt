package com.motisoft.herd.data.repository

import com.motisoft.herd.data.local.dao.CowDao
import com.motisoft.herd.data.local.entity.CowEntity
import com.motisoft.herd.data.remote.dto.CowDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

// v1 sync strategy is deliberately simple: Room is the UI's only read source. Writes
// land in Room immediately (dirty=true) so the UI never blocks on network, then this
// repository attempts an immediate best-effort push. Anything left dirty (offline, or
// the push failed) is retried later by SyncWorker. Last-write-wins by updatedAt, no
// conflict resolution - acceptable because each farm account is single-user.
@Singleton
class CowRepository @Inject constructor(
    private val cowDao: CowDao,
    private val supabase: SupabaseClient,
    private val authRepository: AuthRepository,
) {
    fun observeCows(): Flow<List<CowEntity>> = cowDao.observeAll()

    fun observeCow(tag: String): Flow<CowEntity?> = cowDao.observeByTag(tag)

    suspend fun saveCow(cow: CowEntity) {
        val dirty = cow.copy(dirty = true, updatedAt = Clock.System.now())
        cowDao.upsert(dirty)
        pushCow(dirty)
    }

    suspend fun syncDirty() {
        cowDao.getDirty().forEach { pushCow(it) }
    }

    suspend fun refreshFromRemote() {
        val ownerId = authRepository.currentUserId ?: return
        runCatching {
            val remoteCows = supabase.from("cows")
                .select { filter { eq("owner_id", ownerId) } }
                .decodeList<CowDto>()
            val entities = remoteCows.map { it.toEntity() }
            cowDao.upsertAll(entities)
        }
    }

    private suspend fun pushCow(cow: CowEntity) {
        val ownerId = authRepository.currentUserId ?: return
        runCatching {
            val dto = CowDto(
                id = cow.remoteId,
                ownerId = ownerId,
                tag = cow.tag,
                name = cow.name,
                breed = cow.breed,
                birthDate = cow.birthDate,
                gender = cow.gender,
                weight = cow.weight,
                lineageTag = cow.lineageTag,
                photoUri = cow.photoUri,
            )
            val result = supabase.from("cows")
                .upsert(dto) { onConflict = "owner_id,tag"; select() }
                .decodeSingle<CowDto>()
            cowDao.upsert(cow.copy(remoteId = result.id, dirty = false))
        }
        // On failure the row stays dirty=true; SyncWorker retries on its next run.
    }

    private fun CowDto.toEntity() = CowEntity(
        tag = tag,
        name = name,
        breed = breed,
        birthDate = birthDate,
        gender = gender,
        weight = weight,
        lineageTag = lineageTag,
        photoUri = photoUri,
        remoteId = id,
        updatedAt = updatedAt ?: Clock.System.now(),
        dirty = false,
    )
}
