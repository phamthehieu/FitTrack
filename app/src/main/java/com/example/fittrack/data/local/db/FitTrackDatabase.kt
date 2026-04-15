package com.example.fittrack.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fittrack.data.local.db.dao.DailySummaryDao
import com.example.fittrack.data.local.db.dao.FoodItemDao
import com.example.fittrack.data.local.db.dao.MealItemDao
import com.example.fittrack.data.local.db.dao.MealLogDao
import com.example.fittrack.data.local.db.dao.SyncOutboxDao
import com.example.fittrack.data.local.db.dao.UserProfileDao
import com.example.fittrack.data.local.db.dao.WeightLogDao
import com.example.fittrack.data.local.db.entities.DailySummaryEntity
import com.example.fittrack.data.local.db.entities.FoodItemEntity
import com.example.fittrack.data.local.db.entities.MealItemEntity
import com.example.fittrack.data.local.db.entities.MealLogEntity
import com.example.fittrack.data.local.db.entities.SyncOutboxEntity
import com.example.fittrack.data.local.db.entities.UserProfileEntity
import com.example.fittrack.data.local.db.entities.WeightLogEntity

@Database(
    entities = [
        UserProfileEntity::class,
        WeightLogEntity::class,
        FoodItemEntity::class,
        MealLogEntity::class,
        MealItemEntity::class,
        DailySummaryEntity::class,
        SyncOutboxEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(FitTrackTypeConverters::class)
abstract class FitTrackDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun weightLogDao(): WeightLogDao
    abstract fun foodItemDao(): FoodItemDao
    abstract fun mealLogDao(): MealLogDao
    abstract fun mealItemDao(): MealItemDao
    abstract fun dailySummaryDao(): DailySummaryDao
    abstract fun syncOutboxDao(): SyncOutboxDao
}

