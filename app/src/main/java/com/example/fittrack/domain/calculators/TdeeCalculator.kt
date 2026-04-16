package com.example.fittrack.domain.calculators

object TdeeCalculator {
    /**
     * Mifflin–St Jeor
     * - male: s = +5
     * - female: s = -161
     */
    fun calculateMifflinStJeor(
        weightKg: Float,
        heightCm: Float,
        ageYears: Int,
        isFemale: Boolean,
        activityMultiplier: Double,
    ): Int {
        if (weightKg <= 0f || heightCm <= 0f || ageYears <= 0) return 0
        val s = if (isFemale) -161 else 5
        val bmr = (10f * weightKg) + (6.25f * heightCm) - (5f * ageYears) + s
        return (bmr * activityMultiplier.toFloat()).toInt().coerceAtLeast(0)
    }
}

