package com.example.fittrack.data.remote.firestore

import com.example.fittrack.data.local.db.entities.DailySummaryEntity
import com.example.fittrack.data.local.db.entities.FoodItemEntity
import com.example.fittrack.data.local.db.entities.MealItemEntity
import com.example.fittrack.data.local.db.entities.MealLogEntity
import com.example.fittrack.data.local.db.entities.UserProfileEntity
import com.example.fittrack.data.local.db.entities.WeightLogEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FitTrackFirestoreDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    suspend fun upsertUserProfile(uid: String, entity: UserProfileEntity) {
        firestore.collection(FirestorePaths.userProfiles(uid))
            .document(entity.id.toString())
            .set(entity.toMap())
            .await()
    }

    suspend fun upsertWeightLog(uid: String, entity: WeightLogEntity) {
        firestore.collection(FirestorePaths.weightLogs(uid))
            .document(entity.id.toString())
            .set(entity.toMap())
            .await()
    }

    suspend fun upsertFoodItem(uid: String, entity: FoodItemEntity) {
        firestore.collection(FirestorePaths.foodItems(uid))
            .document(entity.id.toString())
            .set(entity.toMap())
            .await()
    }

    suspend fun upsertMealLog(uid: String, entity: MealLogEntity) {
        firestore.collection(FirestorePaths.mealLogs(uid))
            .document(entity.id.toString())
            .set(entity.toMap())
            .await()
    }

    suspend fun upsertMealItem(uid: String, entity: MealItemEntity) {
        firestore.collection(FirestorePaths.mealItems(uid))
            .document(entity.id.toString())
            .set(entity.toMap())
            .await()
    }

    suspend fun upsertDailySummary(uid: String, entity: DailySummaryEntity) {
        firestore.collection(FirestorePaths.dailySummaries(uid))
            .document(entity.id.toString())
            .set(entity.toMap())
            .await()
    }

    suspend fun pullAll(uid: String): PulledData {
        val profiles = firestore.collection(FirestorePaths.userProfiles(uid)).get().await()
            .documents.mapNotNull { it.toUserProfileEntity() }
        val weights = firestore.collection(FirestorePaths.weightLogs(uid)).get().await()
            .documents.mapNotNull { it.toWeightLogEntity() }
        val foods = firestore.collection(FirestorePaths.foodItems(uid)).get().await()
            .documents.mapNotNull { it.toFoodItemEntity() }
        val mealLogs = firestore.collection(FirestorePaths.mealLogs(uid)).get().await()
            .documents.mapNotNull { it.toMealLogEntity() }
        val mealItems = firestore.collection(FirestorePaths.mealItems(uid)).get().await()
            .documents.mapNotNull { it.toMealItemEntity() }
        val summaries = firestore.collection(FirestorePaths.dailySummaries(uid)).get().await()
            .documents.mapNotNull { it.toDailySummaryEntity() }

        return PulledData(
            userProfiles = profiles,
            weightLogs = weights,
            foodItems = foods,
            mealLogs = mealLogs,
            mealItems = mealItems,
            dailySummaries = summaries,
        )
    }

    data class PulledData(
        val userProfiles: List<UserProfileEntity>,
        val weightLogs: List<WeightLogEntity>,
        val foodItems: List<FoodItemEntity>,
        val mealLogs: List<MealLogEntity>,
        val mealItems: List<MealItemEntity>,
        val dailySummaries: List<DailySummaryEntity>,
    )
}

