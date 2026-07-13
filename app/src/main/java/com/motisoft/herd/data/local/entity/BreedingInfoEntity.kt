package com.motisoft.herd.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "breeding_info",
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
data class BreedingInfoEntity(
    @PrimaryKey val cowTag: String,
    val pregnancyStatus: String,
    val lastHeatDate: LocalDate?,
    val inseminationDate: LocalDate?,
    val pregnancyTestDate: LocalDate?,
    val expectedCalvingDate: LocalDate?,
    val semenBreed: String?,
    val remoteId: String?,
    val updatedAt: Instant,
    val dirty: Boolean,
)
