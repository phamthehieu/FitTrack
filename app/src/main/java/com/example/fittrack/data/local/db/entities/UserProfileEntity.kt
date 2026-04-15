package com.example.fittrack.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val gender: String, // male / female
    @ColumnInfo(name = "birth_date")
    val birthDate: LocalDate?,
    @ColumnInfo(name = "height_cm")
    val heightCm: Double?,
    @ColumnInfo(name = "start_weight")
    val startWeight: Double?,
    @ColumnInfo(name = "target_weight")
    val targetWeight: Double?,
    @ColumnInfo(name = "goal_type")
    val goalType: String, // lose / maintain / gain
    @ColumnInfo(name = "activity_level")
    val activityLevel: Int, // 1–5
    val tdee: Double?,
    @ColumnInfo(name = "daily_calo_target")
    val dailyCaloTarget: Double?,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now(),
)

