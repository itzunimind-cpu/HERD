package com.motisoft.herd.data.remote.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    val name: String? = null,
    val phone: String? = null,
    @SerialName("farm_name") val farmName: String? = null,
    @SerialName("updated_at") val updatedAt: Instant? = null,
)
