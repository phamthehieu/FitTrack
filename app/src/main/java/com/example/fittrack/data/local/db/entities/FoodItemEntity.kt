package com.example.fittrack.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "food_items",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["created_by"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["created_by"]),
        Index(value = ["name"]),
        Index(value = ["category"]),
    ],
)
data class FoodItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "name_en")
    val nameEn: String?,
    val category: String?,
    @ColumnInfo(name = "serving_size_g")
    val servingSizeG: Double?,
    val calories: Double, // per 100g
    @ColumnInfo(name = "protein_g")
    val proteinG: Double?,
    @ColumnInfo(name = "carb_g")
    val carbG: Double?,
    @ColumnInfo(name = "fat_g")
    val fatG: Double?,
    @ColumnInfo(name = "fiber_g")
    val fiberG: Double?,
    @ColumnInfo(name = "is_custom")
    val isCustom: Boolean = false,
    @ColumnInfo(name = "created_by")
    val createdBy: Long?,
)

