package com.motisoft.herd.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.motisoft.herd.data.local.entity.CowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CowDao {
    @Query("SELECT * FROM cows ORDER BY name")
    fun observeAll(): Flow<List<CowEntity>>

    @Query("SELECT * FROM cows WHERE tag = :tag")
    fun observeByTag(tag: String): Flow<CowEntity?>

    @Query("SELECT * FROM cows WHERE tag = :tag")
    suspend fun getByTag(tag: String): CowEntity?

    @Query("SELECT * FROM cows WHERE dirty = 1")
    suspend fun getDirty(): List<CowEntity>

    @Upsert
    suspend fun upsert(cow: CowEntity)

    @Upsert
    suspend fun upsertAll(cows: List<CowEntity>)

    @Delete
    suspend fun delete(cow: CowEntity)
}
