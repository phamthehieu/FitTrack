package com.example.fittrack.data.sync

import android.content.Context
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fittrack.data.local.db.DatabaseProvider
import com.example.fittrack.data.remote.firestore.FitTrackFirestoreDataSource
import com.example.fittrack.data.repository.FitTrackRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class FitTrackSyncWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@withContext Result.success()

        val db = DatabaseProvider.get(applicationContext)
        val outboxDao = db.syncOutboxDao()
        val remote = FitTrackFirestoreDataSource()

        // Retry lại các item FAILED (để mạng chập chờn vẫn tự hồi)
        outboxDao.retryAllFailed()

        val pending = outboxDao.nextPending(limit = 50)
        for (item in pending) {
            try {
                when (item.entityType) {
                    FitTrackRepository.ENTITY_USER_PROFILES -> {
                        val entity = db.userProfileDao().getById(item.entityId) ?: run {
                            outboxDao.markSynced(item.id)
                            continue
                        }
                        remote.upsertUserProfile(uid, entity)
                    }
                    FitTrackRepository.ENTITY_WEIGHT_LOGS -> {
                        val entity = db.weightLogDao().getForId(item.entityId) ?: run {
                            outboxDao.markSynced(item.id)
                            continue
                        }
                        remote.upsertWeightLog(uid, entity)
                    }
                    FitTrackRepository.ENTITY_FOOD_ITEMS -> {
                        val entity = db.foodItemDao().getById(item.entityId) ?: run {
                            outboxDao.markSynced(item.id)
                            continue
                        }
                        remote.upsertFoodItem(uid, entity)
                    }
                    FitTrackRepository.ENTITY_MEAL_LOGS -> {
                        val entity = db.mealLogDao().getById(item.entityId) ?: run {
                            outboxDao.markSynced(item.id)
                            continue
                        }
                        remote.upsertMealLog(uid, entity)
                    }
                    FitTrackRepository.ENTITY_MEAL_ITEMS -> {
                        val entity = db.mealItemDao().getById(item.entityId) ?: run {
                            outboxDao.markSynced(item.id)
                            continue
                        }
                        remote.upsertMealItem(uid, entity)
                    }
                    FitTrackRepository.ENTITY_DAILY_SUMMARIES -> {
                        val entity = db.dailySummaryDao().getById(item.entityId) ?: run {
                            outboxDao.markSynced(item.id)
                            continue
                        }
                        remote.upsertDailySummary(uid, entity)
                    }
                    else -> {
                        outboxDao.markSynced(item.id)
                        continue
                    }
                }
                outboxDao.markSynced(item.id)
            } catch (_: Throwable) {
                outboxDao.markFailed(item.id, Instant.now())
                return@withContext Result.retry()
            }
        }

        // Pull remote về local (MVP: kéo toàn bộ)
        try {
            val pulled = remote.pullAll(uid)
            db.withTransaction {
                pulled.userProfiles.forEach { db.userProfileDao().upsert(it) }
                pulled.weightLogs.forEach { db.weightLogDao().upsert(it) }
                pulled.foodItems.forEach { db.foodItemDao().upsert(it) }
                pulled.mealLogs.forEach { db.mealLogDao().upsert(it) }
                pulled.mealItems.forEach { db.mealItemDao().upsert(it) }
                pulled.dailySummaries.forEach { db.dailySummaryDao().upsert(it) }
            }
        } catch (_: Throwable) {
            // Pull lỗi vẫn không phá UX; lần sau worker sẽ thử lại
        }

        outboxDao.deleteSynced()
        Result.success()
    }
}

