package com.motisoft.herd.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "milk_records",
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
data class MilkRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cowTag: String,
    val date: LocalDate,
    val morningYield: Double,
    val eveningYield: Double,
    val fatPct: Double?,
    val snfPct: Double?,
    val remoteId: String?,
    val updatedAt: Instant,
    val dirty: Boolean,
)
