package com.example.fittrack.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fittrack.data.local.db.entities.WeightLogEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface WeightLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(log: WeightLogEntity): Long

    @Query(
        """
        SELECT * FROM weight_logs
        WHERE user_id = :userId
        ORDER BY log_date DESC
        """
    )
    fun observeForUser(userId: Long): Flow<List<WeightLogEntity>>

    @Query(
        """
        SELECT * FROM weight_logs
        WHERE user_id = :userId AND log_date = :date
        LIMIT 1
        """
    )
    suspend fun getForUserOnDate(userId: Long, date: LocalDate): WeightLogEntity?

    @Query("SELECT * FROM weight_logs WHERE id = :id LIMIT 1")
    suspend fun getForId(id: Long): WeightLogEntity?
}

