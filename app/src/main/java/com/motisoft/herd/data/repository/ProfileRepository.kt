package com.motisoft.herd.data.repository

import com.motisoft.herd.data.local.dao.ProfileDao
import com.motisoft.herd.data.local.entity.ProfileEntity
import com.motisoft.herd.data.remote.dto.ProfileDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

// Same local-first, dirty-flag sync strategy as CowRepository, but for the single
// profile row (name/phone/farm name) belonging to the signed-in farmer.
@Singleton
class ProfileRepository @Inject constructor(
    private val profileDao: ProfileDao,
    private val supabase: SupabaseClient,
    private val authRepository: AuthRepository,
) {
    fun observeProfile(): Flow<ProfileEntity?> {
        val ownerId = authRepository.currentUserId ?: return flowOf(null)
        return profileDao.observeByOwner(ownerId)
    }

    suspend fun saveProfile(name: String, phone: String, farmName: String) {
        val ownerId = authRepository.currentUserId ?: return
        val dirty = ProfileEntity(
            ownerId = ownerId,
            name = name,
            phone = phone,
            farmName = farmName,
            updatedAt = Clock.System.now(),
            dirty = true,
        )
        profileDao.upsert(dirty)
        pushProfile(dirty)
    }

    suspend fun syncDirty() {
        profileDao.getDirty().forEach { pushProfile(it) }
    }

    suspend fun refreshFromRemote() {
        val ownerId = authRepository.currentUserId ?: return
        runCatching {
            val remoteProfile = supabase.from("profiles")
                .select { filter { eq("id", ownerId) } }
                .decodeSingleOrNull<ProfileDto>()
            if (remoteProfile != null) {
                profileDao.upsert(remoteProfile.toEntity())
            }
        }
    }

    private suspend fun pushProfile(profile: ProfileEntity) {
        runCatching {
            val dto = ProfileDto(
                id = profile.ownerId,
                name = profile.name,
                phone = profile.phone,
                farmName = profile.farmName,
            )
            supabase.from("profiles").upsert(dto) { onConflict = "id" }
            profileDao.upsert(profile.copy(dirty = false))
        }
        // On failure the row stays dirty=true; SyncWorker retries on its next run.
    }

    private fun ProfileDto.toEntity() = ProfileEntity(
        ownerId = id,
        name = name.orEmpty(),
        phone = phone.orEmpty(),
        farmName = farmName.orEmpty(),
        updatedAt = updatedAt ?: Clock.System.now(),
        dirty = false,
    )
}
