package com.example.fittrack.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fittrack.data.local.db.entities.MealLogEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface MealLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(mealLog: MealLogEntity): Long

    @Query(
        """
        SELECT * FROM meal_logs
        WHERE user_id = :userId AND log_date = :date
        ORDER BY meal_type
        """
    )
    fun observeForUserOnDate(userId: Long, date: LocalDate): Flow<List<MealLogEntity>>

    @Query(
        """
        SELECT * FROM meal_logs
        WHERE user_id = :userId
        ORDER BY log_date DESC, logged_at DESC
        """
    )
    fun observeForUser(userId: Long): Flow<List<MealLogEntity>>

    @Query("SELECT * FROM meal_logs WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): MealLogEntity?
}

