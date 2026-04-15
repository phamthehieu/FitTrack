package com.example.fittrack.data.remote.firestore

import com.example.fittrack.data.local.db.entities.DailySummaryEntity
import com.example.fittrack.data.local.db.entities.FoodItemEntity
import com.example.fittrack.data.local.db.entities.MealItemEntity
import com.example.fittrack.data.local.db.entities.MealLogEntity
import com.example.fittrack.data.local.db.entities.UserProfileEntity
import com.example.fittrack.data.local.db.entities.WeightLogEntity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.time.Instant
import java.time.LocalDate

internal fun Instant.toFirestoreValue(): Timestamp = Timestamp(this.epochSecond, this.nano)
internal fun Timestamp.toJavaInstant(): Instant = Instant.ofEpochSecond(seconds, nanoseconds.toLong())

internal fun LocalDate.toIso(): String = toString()
internal fun String.toLocalDateOrNull(): LocalDate? = runCatching { LocalDate.parse(this) }.getOrNull()

internal fun UserProfileEntity.toMap(): Map<String, Any?> =
    mapOf(
        "id" to id,
        "name" to name,
        "gender" to gender,
        "birth_date" to birthDate?.toIso(),
        "height_cm" to heightCm,
        "start_weight" to startWeight,
        "target_weight" to targetWeight,
        "goal_type" to goalType,
        "activity_level" to activityLevel,
        "tdee" to tdee,
        "daily_calo_target" to dailyCaloTarget,
        "created_at" to createdAt.toFirestoreValue(),
        "updated_at" to Timestamp.now(),
    )

internal fun WeightLogEntity.toMap(): Map<String, Any?> =
    mapOf(
        "id" to id,
        "user_id" to userId,
        "log_date" to logDate.toIso(),
        "weight_kg" to weightKg,
        "bmi" to bmi,
        "note" to note,
        "logged_at" to loggedAt.toFirestoreValue(),
        "updated_at" to Timestamp.now(),
    )

internal fun FoodItemEntity.toMap(): Map<String, Any?> =
    mapOf(
        "id" to id,
        "name" to name,
        "name_en" to nameEn,
        "category" to category,
        "serving_size_g" to servingSizeG,
        "calories" to calories,
        "protein_g" to proteinG,
        "carb_g" to carbG,
        "fat_g" to fatG,
        "fiber_g" to fiberG,
        "is_custom" to isCustom,
        "created_by" to createdBy,
        "updated_at" to Timestamp.now(),
    )

internal fun MealLogEntity.toMap(): Map<String, Any?> =
    mapOf(
        "id" to id,
        "user_id" to userId,
        "log_date" to logDate.toIso(),
        "meal_type" to mealType,
        "total_calories" to totalCalories,
        "logged_at" to loggedAt.toFirestoreValue(),
        "updated_at" to Timestamp.now(),
    )

internal fun MealItemEntity.toMap(): Map<String, Any?> =
    mapOf(
        "id" to id,
        "meal_log_id" to mealLogId,
        "food_item_id" to foodItemId,
        "quantity_g" to quantityG,
        "calories_actual" to caloriesActual,
        "protein_actual" to proteinActual,
        "carb_actual" to carbActual,
        "fat_actual" to fatActual,
        "updated_at" to Timestamp.now(),
    )

internal fun DailySummaryEntity.toMap(): Map<String, Any?> =
    mapOf(
        "id" to id,
        "user_id" to userId,
        "summary_date" to summaryDate.toIso(),
        "total_calories" to totalCalories,
        "total_protein" to totalProtein,
        "total_carb" to totalCarb,
        "total_fat" to totalFat,
        "weight_kg" to weightKg,
        "calo_deficit" to caloDeficit,
        "updated_at" to Timestamp.now(),
    )

internal fun DocumentSnapshot.toUserProfileEntity(): UserProfileEntity? {
    val id = getLong("id") ?: return null
    return UserProfileEntity(
        id = id,
        name = getString("name") ?: return null,
        gender = getString("gender") ?: return null,
        birthDate = getString("birth_date")?.toLocalDateOrNull(),
        heightCm = getDouble("height_cm"),
        startWeight = getDouble("start_weight"),
        targetWeight = getDouble("target_weight"),
        goalType = getString("goal_type") ?: "maintain",
        activityLevel = (getLong("activity_level") ?: 1L).toInt(),
        tdee = getDouble("tdee"),
        dailyCaloTarget = getDouble("daily_calo_target"),
        createdAt = (getTimestamp("created_at") ?: Timestamp.now()).toJavaInstant(),
    )
}

internal fun DocumentSnapshot.toWeightLogEntity(): WeightLogEntity? {
    val id = getLong("id") ?: return null
    val userId = getLong("user_id") ?: return null
    val logDate = getString("log_date")?.toLocalDateOrNull() ?: return null
    val weightKg = getDouble("weight_kg") ?: return null
    return WeightLogEntity(
        id = id,
        userId = userId,
        logDate = logDate,
        weightKg = weightKg,
        bmi = getDouble("bmi"),
        note = getString("note"),
        loggedAt = (getTimestamp("logged_at") ?: Timestamp.now()).toJavaInstant(),
    )
}

internal fun DocumentSnapshot.toFoodItemEntity(): FoodItemEntity? {
    val id = getLong("id") ?: return null
    val name = getString("name") ?: return null
    val calories = getDouble("calories") ?: return null
    return FoodItemEntity(
        id = id,
        name = name,
        nameEn = getString("name_en"),
        category = getString("category"),
        servingSizeG = getDouble("serving_size_g"),
        calories = calories,
        proteinG = getDouble("protein_g"),
        carbG = getDouble("carb_g"),
        fatG = getDouble("fat_g"),
        fiberG = getDouble("fiber_g"),
        isCustom = getBoolean("is_custom") ?: false,
        createdBy = getLong("created_by"),
    )
}

internal fun DocumentSnapshot.toMealLogEntity(): MealLogEntity? {
    val id = getLong("id") ?: return null
    val userId = getLong("user_id") ?: return null
    val logDate = getString("log_date")?.toLocalDateOrNull() ?: return null
    val mealType = getString("meal_type") ?: return null
    return MealLogEntity(
        id = id,
        userId = userId,
        logDate = logDate,
        mealType = mealType,
        totalCalories = getDouble("total_calories"),
        loggedAt = (getTimestamp("logged_at") ?: Timestamp.now()).toJavaInstant(),
    )
}

internal fun DocumentSnapshot.toMealItemEntity(): MealItemEntity? {
    val id = getLong("id") ?: return null
    val mealLogId = getLong("meal_log_id") ?: return null
    val foodItemId = getLong("food_item_id") ?: return null
    val quantityG = getDouble("quantity_g") ?: return null
    return MealItemEntity(
        id = id,
        mealLogId = mealLogId,
        foodItemId = foodItemId,
        quantityG = quantityG,
        caloriesActual = getDouble("calories_actual"),
        proteinActual = getDouble("protein_actual"),
        carbActual = getDouble("carb_actual"),
        fatActual = getDouble("fat_actual"),
    )
}

internal fun DocumentSnapshot.toDailySummaryEntity(): DailySummaryEntity? {
    val id = getLong("id") ?: return null
    val userId = getLong("user_id") ?: return null
    val date = getString("summary_date")?.toLocalDateOrNull() ?: return null
    return DailySummaryEntity(
        id = id,
        userId = userId,
        summaryDate = date,
        totalCalories = getDouble("total_calories"),
        totalProtein = getDouble("total_protein"),
        totalCarb = getDouble("total_carb"),
        totalFat = getDouble("total_fat"),
        weightKg = getDouble("weight_kg"),
        caloDeficit = getDouble("calo_deficit"),
    )
}

