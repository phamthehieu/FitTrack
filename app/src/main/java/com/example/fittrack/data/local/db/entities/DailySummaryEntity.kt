package com.example.fittrack.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "daily_summaries",
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
        Index(value = ["user_id", "summary_date"], unique = true),
    ],
)
data class DailySummaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "user_id")
    val userId: Long,
    @ColumnInfo(name = "summary_date")
    val summaryDate: LocalDate,
    @ColumnInfo(name = "total_calories")
    val totalCalories: Double?,
    @ColumnInfo(name = "total_protein")
    val totalProtein: Double?,
    @ColumnInfo(name = "total_carb")
    val totalCarb: Double?,
    @ColumnInfo(name = "total_fat")
    val totalFat: Double?,
    @ColumnInfo(name = "weight_kg")
    val weightKg: Double?,
    @ColumnInfo(name = "calo_deficit")
    val caloDeficit: Double?,
)

