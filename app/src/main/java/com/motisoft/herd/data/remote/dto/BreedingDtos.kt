package com.motisoft.herd.data.remote.dto

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BreedingInfoDto(
    val id: String? = null,
    @SerialName("owner_id") val ownerId: String? = null,
    @SerialName("cow_id") val cowId: String,
    @SerialName("pregnancy_status") val pregnancyStatus: String,
    @SerialName("last_heat_date") val lastHeatDate: LocalDate? = null,
    @SerialName("insemination_date") val inseminationDate: LocalDate? = null,
    @SerialName("expected_calving_date") val expectedCalvingDate: LocalDate? = null,
    @SerialName("updated_at") val updatedAt: Instant? = null,
)

@Serializable
data class CalvingHistoryDto(
    val id: String? = null,
    @SerialName("owner_id") val ownerId: String? = null,
    @SerialName("cow_id") val cowId: String,
    @SerialName("calf_sex") val calfSex: String,
    @SerialName("calving_date") val calvingDate: LocalDate,
    @SerialName("updated_at") val updatedAt: Instant? = null,
)
