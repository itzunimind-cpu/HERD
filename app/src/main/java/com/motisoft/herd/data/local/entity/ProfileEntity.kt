package com.motisoft.herd.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

// PK is the Supabase auth user id - there is exactly one row per signed-in farmer.
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val ownerId: String,
    val name: String,
    val phone: String,
    val farmName: String,
    val updatedAt: Instant,
    val dirty: Boolean,
)
