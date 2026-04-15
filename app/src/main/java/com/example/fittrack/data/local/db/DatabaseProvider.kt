package com.example.fittrack.data.local.db

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: FitTrackDatabase? = null

    fun get(context: Context): FitTrackDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                FitTrackDatabase::class.java,
                "fittrack.db",
            )
                .addMigrations(FitTrackMigrations.MIGRATION_1_2)
                .build()
                .also { INSTANCE = it }
        }
    }
}

