package com.example.fittrack.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.fittrack.data.local.db.entities.SyncOutboxEntity
import java.time.Instant

@Dao
interface SyncOutboxDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun enqueue(item: SyncOutboxEntity): Long

    @Transaction
    suspend fun enqueueUpsert(userUid: String, entityType: String, entityId: Long) {
        enqueue(
            SyncOutboxEntity(
                userUid = userUid,
                entityType = entityType,
                entityId = entityId,
                operation = "UPSERT",
                status = "PENDING",
            )
        )
    }

    @Query(
        """
        SELECT * FROM sync_outbox
        WHERE status = 'PENDING'
        ORDER BY created_at ASC
        LIMIT :limit
        """
    )
    suspend fun nextPending(limit: Int): List<SyncOutboxEntity>

    @Update
    suspend fun update(item: SyncOutboxEntity)

    @Query("DELETE FROM sync_outbox WHERE status = 'SYNCED'")
    suspend fun deleteSynced()

    @Query("UPDATE sync_outbox SET status = 'SYNCED' WHERE id = :id")
    suspend fun markSynced(id: Long)

    @Query(
        """
        UPDATE sync_outbox
        SET status = 'FAILED',
            attempt_count = attempt_count + 1,
            last_attempt_at = :attemptAt
        WHERE id = :id
        """
    )
    suspend fun markFailed(id: Long, attemptAt: Instant)

    @Query("UPDATE sync_outbox SET status = 'PENDING' WHERE status = 'FAILED'")
    suspend fun retryAllFailed()
}

