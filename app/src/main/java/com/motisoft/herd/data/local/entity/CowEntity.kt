package com.motisoft.herd.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

// Room's PK is the human-facing tag string - safe locally since Room only ever
// caches the signed-in farmer's own rows. The Postgres side uses a surrogate UUID
// instead, because tags are only unique per-farmer, not globally (see CowRepository).
@Entity(tableName = "cows")
data class CowEntity(
    @PrimaryKey val tag: String,
    val name: String,
    val breed: String,
    val birthDate: LocalDate?,
    val gender: String,
    val weight: Double?,
    val lineageTag: String?,
    val photoUri: String?,
    val remoteId: String?,
    val updatedAt: Instant,
    val dirty: Boolean,
)
