package com.motisoft.herd.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "daily_logs",
    foreignKeys = [
        ForeignKey(
            entity = CowEntity::class,
            parentColumns = ["tag"],
            childColumns = ["cowTag"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("cowTag"), Index(value = ["cowTag", "date"], unique = true)],
)
data class DailyLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cowTag: String,
    val date: LocalDate,
    val feed: String,
    val water: String,
    val temperature: Double?,
    val activityNotes: String,
    val remoteId: String?,
    val updatedAt: Instant,
    val dirty: Boolean,
)
