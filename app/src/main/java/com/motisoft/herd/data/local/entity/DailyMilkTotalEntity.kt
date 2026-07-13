package com.motisoft.herd.data.local.entity

import androidx.room.Entity
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

// Farm-wide total for a given day - not tied to any specific cow, unlike MilkRecordEntity.
@Entity(tableName = "daily_milk_totals", primaryKeys = ["ownerId", "date"])
data class DailyMilkTotalEntity(
    val ownerId: String,
    val date: LocalDate,
    val morningTotal: Double?,
    val eveningTotal: Double?,
    val remoteId: String?,
    val updatedAt: Instant,
    val dirty: Boolean,
)
