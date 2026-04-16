package com.example.fittrack.domain.calculators

object BmiCalculator {
    fun calculate(heightCm: Float, weightKg: Float): Float {
        val hM = heightCm / 100f
        return if (hM > 0f && weightKg > 0f) (weightKg / (hM * hM)) else 0f
    }
}

