package com.motisoft.herd.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "calving_history",
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
data class CalvingHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cowTag: String,
    val calfSex: String,
    val calvingDate: LocalDate,
    val remoteId: String?,
    val updatedAt: Instant,
    val dirty: Boolean,
)
