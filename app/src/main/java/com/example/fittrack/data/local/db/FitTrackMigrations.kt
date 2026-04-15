package com.example.fittrack.data.local.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object FitTrackMigrations {
    val MIGRATION_1_2: Migration =
        object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `sync_outbox` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `user_uid` TEXT NOT NULL,
                        `entity_type` TEXT NOT NULL,
                        `entity_id` INTEGER NOT NULL,
                        `operation` TEXT NOT NULL,
                        `status` TEXT NOT NULL,
                        `attempt_count` INTEGER NOT NULL,
                        `last_attempt_at` INTEGER,
                        `created_at` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_sync_outbox_user_uid` ON `sync_outbox` (`user_uid`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_sync_outbox_status_created_at` ON `sync_outbox` (`status`, `created_at`)")
            }
        }
}

