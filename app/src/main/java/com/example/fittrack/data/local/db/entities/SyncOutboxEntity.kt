package com.example.fittrack.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Outbox pattern: mọi thay đổi được ghi vào local trước, sau đó sync nền lên Firebase.
 * Worker sẽ đọc hàng đợi này và thực hiện push lên remote theo thứ tự thời gian.
 */
@Entity(
    tableName = "sync_outbox",
    indices = [
        Index(value = ["user_uid"]),
        Index(value = ["status", "created_at"]),
    ],
)
data class SyncOutboxEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "user_uid")
    val userUid: String,
    @ColumnInfo(name = "entity_type")
    val entityType: String, // user_profiles / weight_logs / ...
    @ColumnInfo(name = "entity_id")
    val entityId: Long,
    val operation: String = "UPSERT", // MVP: chỉ upsert
    val status: String = "PENDING", // PENDING / SYNCED / FAILED
    @ColumnInfo(name = "attempt_count")
    val attemptCount: Int = 0,
    @ColumnInfo(name = "last_attempt_at")
    val lastAttemptAt: Instant? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now(),
)

