package com.motisoft.herd.data.remote.dto

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HealthStatusDto(
    val id: String? = null,
    @SerialName("owner_id") val ownerId: String? = null,
    @SerialName("cow_id") val cowId: String,
    @SerialName("current_status") val currentStatus: String,
    @SerialName("updated_at") val updatedAt: Instant? = null,
)

@Serializable
data class VaccinationDto(
    val id: String? = null,
    @SerialName("owner_id") val ownerId: String? = null,
    @SerialName("cow_id") val cowId: String,
    @SerialName("vaccine_name") val vaccineName: String,
    val date: LocalDate,
    @SerialName("updated_at") val updatedAt: Instant? = null,
)

@Serializable
data class IllnessLogDto(
    val id: String? = null,
    @SerialName("owner_id") val ownerId: String? = null,
    @SerialName("cow_id") val cowId: String,
    val description: String,
    val date: LocalDate,
    val treatment: String,
    @SerialName("updated_at") val updatedAt: Instant? = null,
)
