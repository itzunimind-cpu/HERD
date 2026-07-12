package com.motisoft.herd.data.remote.dto

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MilkRecordDto(
    val id: String? = null,
    @SerialName("owner_id") val ownerId: String? = null,
    @SerialName("cow_id") val cowId: String,
    val date: LocalDate,
    @SerialName("morning_yield") val morningYield: Double,
    @SerialName("evening_yield") val eveningYield: Double,
    @SerialName("fat_pct") val fatPct: Double? = null,
    @SerialName("snf_pct") val snfPct: Double? = null,
    @SerialName("updated_at") val updatedAt: Instant? = null,
)
