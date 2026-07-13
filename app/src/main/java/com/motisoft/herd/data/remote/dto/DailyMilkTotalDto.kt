package com.motisoft.herd.data.remote.dto

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyMilkTotalDto(
    val id: String? = null,
    @SerialName("owner_id") val ownerId: String? = null,
    val date: LocalDate,
    @SerialName("morning_total") val morningTotal: Double? = null,
    @SerialName("evening_total") val eveningTotal: Double? = null,
    @SerialName("updated_at") val updatedAt: Instant? = null,
)
