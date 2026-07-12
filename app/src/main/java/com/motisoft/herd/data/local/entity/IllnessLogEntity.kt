package com.motisoft.herd.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "illness_log",
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
data class IllnessLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cowTag: String,
    val description: String,
    val date: LocalDate,
    val treatment: String,
    val remoteId: String?,
    val updatedAt: Instant,
    val dirty: Boolean,
)
