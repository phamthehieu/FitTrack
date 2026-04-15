package com.example.fittrack.data.repository

import com.example.fittrack.data.local.db.FitTrackDatabase
import com.example.fittrack.data.local.db.entities.DailySummaryEntity
import com.example.fittrack.data.local.db.entities.FoodItemEntity
import com.example.fittrack.data.local.db.entities.MealItemEntity
import com.example.fittrack.data.local.db.entities.MealLogEntity
import com.example.fittrack.data.local.db.entities.UserProfileEntity
import com.example.fittrack.data.local.db.entities.WeightLogEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow

/**
 * Offline-first:
 * - UI đọc từ Room
 * - Mọi ghi (create/update) -> Room trước, rồi enqueue outbox để Worker sync lên Firebase khi có mạng.
 */
class FitTrackRepository(
    private val db: FitTrackDatabase,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {
    private fun requireUid(): String =
        auth.currentUser?.uid ?: error("Chưa đăng nhập: không có Firebase uid để sync")

    fun observeProfiles(): Flow<List<UserProfileEntity>> = db.userProfileDao().observeAll()

    suspend fun upsertUserProfile(profile: UserProfileEntity): Long {
        val id = db.userProfileDao().upsert(profile)
        db.syncOutboxDao().enqueueUpsert(requireUid(), ENTITY_USER_PROFILES, id)
        return id
    }

    fun observeWeightLogs(userId: Long): Flow<List<WeightLogEntity>> = db.weightLogDao().observeForUser(userId)

    suspend fun upsertWeightLog(log: WeightLogEntity): Long {
        val id = db.weightLogDao().upsert(log)
        db.syncOutboxDao().enqueueUpsert(requireUid(), ENTITY_WEIGHT_LOGS, id)
        return id
    }

    fun observeFoodItems(includeCustom: Boolean = true): Flow<List<FoodItemEntity>> =
        db.foodItemDao().observeAll(includeCustom)

    suspend fun upsertFoodItem(item: FoodItemEntity): Long {
        val id = db.foodItemDao().upsert(item)
        db.syncOutboxDao().enqueueUpsert(requireUid(), ENTITY_FOOD_ITEMS, id)
        return id
    }

    fun observeMealLogs(userId: Long): Flow<List<MealLogEntity>> = db.mealLogDao().observeForUser(userId)

    suspend fun upsertMealLog(mealLog: MealLogEntity): Long {
        val id = db.mealLogDao().upsert(mealLog)
        db.syncOutboxDao().enqueueUpsert(requireUid(), ENTITY_MEAL_LOGS, id)
        return id
    }

    fun observeMealItems(mealLogId: Long): Flow<List<MealItemEntity>> = db.mealItemDao().observeForMealLog(mealLogId)

    suspend fun upsertMealItem(item: MealItemEntity): Long {
        val id = db.mealItemDao().upsert(item)
        db.syncOutboxDao().enqueueUpsert(requireUid(), ENTITY_MEAL_ITEMS, id)
        return id
    }

    fun observeDailySummaries(userId: Long): Flow<List<DailySummaryEntity>> = db.dailySummaryDao().observeForUser(userId)

    suspend fun upsertDailySummary(summary: DailySummaryEntity): Long {
        val id = db.dailySummaryDao().upsert(summary)
        db.syncOutboxDao().enqueueUpsert(requireUid(), ENTITY_DAILY_SUMMARIES, id)
        return id
    }

    companion object {
        const val ENTITY_USER_PROFILES = "user_profiles"
        const val ENTITY_WEIGHT_LOGS = "weight_logs"
        const val ENTITY_FOOD_ITEMS = "food_items"
        const val ENTITY_MEAL_LOGS = "meal_logs"
        const val ENTITY_MEAL_ITEMS = "meal_items"
        const val ENTITY_DAILY_SUMMARIES = "daily_summaries"
    }
}

