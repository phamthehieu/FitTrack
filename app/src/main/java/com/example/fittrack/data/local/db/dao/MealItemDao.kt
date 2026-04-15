package com.example.fittrack.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fittrack.data.local.db.entities.MealItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: MealItemEntity): Long

    @Query(
        """
        SELECT * FROM meal_items
        WHERE meal_log_id = :mealLogId
        """
    )
    fun observeForMealLog(mealLogId: Long): Flow<List<MealItemEntity>>

    @Query("SELECT * FROM meal_items WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): MealItemEntity?
}

