package com.example.fittrack.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meal_items",
    foreignKeys = [
        ForeignKey(
            entity = MealLogEntity::class,
            parentColumns = ["id"],
            childColumns = ["meal_log_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = FoodItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["food_item_id"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["meal_log_id"]),
        Index(value = ["food_item_id"]),
    ],
)
data class MealItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "meal_log_id")
    val mealLogId: Long,
    @ColumnInfo(name = "food_item_id")
    val foodItemId: Long,
    @ColumnInfo(name = "quantity_g")
    val quantityG: Double,
    @ColumnInfo(name = "calories_actual")
    val caloriesActual: Double?,
    @ColumnInfo(name = "protein_actual")
    val proteinActual: Double?,
    @ColumnInfo(name = "carb_actual")
    val carbActual: Double?,
    @ColumnInfo(name = "fat_actual")
    val fatActual: Double?,
)

