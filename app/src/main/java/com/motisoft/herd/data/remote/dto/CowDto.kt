package com.motisoft.herd.data.remote.dto

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CowDto(
    val id: String? = null,
    @SerialName("owner_id") val ownerId: String? = null,
    val tag: String,
    val name: String,
    val breed: String,
    @SerialName("birth_date") val birthDate: LocalDate? = null,
    val gender: String,
    val weight: Double? = null,
    @SerialName("lineage_tag") val lineageTag: String? = null,
    @SerialName("photo_uri") val photoUri: String? = null,
    @SerialName("updated_at") val updatedAt: Instant? = null,
)
