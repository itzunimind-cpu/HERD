package com.motisoft.herd.data.remote.dto

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyLogDto(
    val id: String? = null,
    @SerialName("owner_id") val ownerId: String? = null,
    @SerialName("cow_id") val cowId: String,
    val date: LocalDate,
    val feed: String,
    val water: String,
    val temperature: Double? = null,
    @SerialName("activity_notes") val activityNotes: String,
    @SerialName("updated_at") val updatedAt: Instant? = null,
)
