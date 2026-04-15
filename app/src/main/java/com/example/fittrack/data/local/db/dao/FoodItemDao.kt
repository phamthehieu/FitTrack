package com.example.fittrack.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fittrack.data.local.db.entities.FoodItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: FoodItemEntity): Long

    @Query("SELECT * FROM food_items WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): FoodItemEntity?

    @Query(
        """
        SELECT * FROM food_items
        WHERE (:includeCustom = 1 OR is_custom = 0)
        ORDER BY name COLLATE NOCASE
        """
    )
    fun observeAll(includeCustom: Boolean = true): Flow<List<FoodItemEntity>>

    @Query(
        """
        SELECT * FROM food_items
        WHERE name LIKE '%' || :q || '%' OR name_en LIKE '%' || :q || '%'
        ORDER BY name COLLATE NOCASE
        """
    )
    fun search(q: String): Flow<List<FoodItemEntity>>
}

