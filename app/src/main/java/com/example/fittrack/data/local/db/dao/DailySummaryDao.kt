package com.example.fittrack.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fittrack.data.local.db.entities.DailySummaryEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DailySummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(summary: DailySummaryEntity): Long

    @Query(
        """
        SELECT * FROM daily_summaries
        WHERE user_id = :userId AND summary_date = :date
        LIMIT 1
        """
    )
    suspend fun getForUserOnDate(userId: Long, date: LocalDate): DailySummaryEntity?

    @Query(
        """
        SELECT * FROM daily_summaries
        WHERE user_id = :userId
        ORDER BY summary_date DESC
        """
    )
    fun observeForUser(userId: Long): Flow<List<DailySummaryEntity>>

    @Query("SELECT * FROM daily_summaries WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): DailySummaryEntity?
}

