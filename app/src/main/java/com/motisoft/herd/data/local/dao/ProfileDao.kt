package com.motisoft.herd.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.motisoft.herd.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles WHERE ownerId = :ownerId")
    fun observeByOwner(ownerId: String): Flow<ProfileEntity?>

    @Query("SELECT * FROM profiles WHERE dirty = 1")
    suspend fun getDirty(): List<ProfileEntity>

    @Upsert
    suspend fun upsert(profile: ProfileEntity)
}
