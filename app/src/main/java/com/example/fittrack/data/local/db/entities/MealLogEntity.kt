package com.example.fittrack.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(
    tableName = "meal_logs",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["user_id", "log_date"]),
        Index(value = ["meal_type"]),
    ],
)
data class MealLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "user_id")
    val userId: Long,
    @ColumnInfo(name = "log_date")
    val logDate: LocalDate,
    @ColumnInfo(name = "meal_type")
    val mealType: String, // breakfast / lunch / dinner / snack
    @ColumnInfo(name = "total_calories")
    val totalCalories: Double?,
    @ColumnInfo(name = "logged_at")
    val loggedAt: Instant = Instant.now(),
)

