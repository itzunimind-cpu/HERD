package com.motisoft.herd.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(
    tableName = "health_status",
    foreignKeys = [
        ForeignKey(
            entity = CowEntity::class,
            parentColumns = ["tag"],
            childColumns = ["cowTag"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("cowTag")],
)
data class HealthStatusEntity(
    @PrimaryKey val cowTag: String,
    val currentStatus: String,
    val remoteId: String?,
    val updatedAt: Instant,
    val dirty: Boolean,
)
