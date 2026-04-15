package com.example.fittrack.data.remote.firestore

/**
 * Quy ước: dữ liệu thuộc 1 user nằm dưới `users/{uid}`.
 * Mỗi bảng Room tương ứng 1 collection con.
 */
object FirestorePaths {
    fun userDoc(uid: String) = "users/$uid"

    fun userProfiles(uid: String) = "${userDoc(uid)}/user_profiles"
    fun weightLogs(uid: String) = "${userDoc(uid)}/weight_logs"
    fun foodItems(uid: String) = "${userDoc(uid)}/food_items"
    fun mealLogs(uid: String) = "${userDoc(uid)}/meal_logs"
    fun mealItems(uid: String) = "${userDoc(uid)}/meal_items"
    fun dailySummaries(uid: String) = "${userDoc(uid)}/daily_summaries"
}

