package com.example.fittrack.ui.screens.onboarding

data class OnboardingUiState(
    val fullName: String = "",
    val age: String = "",
    val genderLabel: String = "",
    val heightCmText: String = "",
    val weightKgText: String = "",
    val selectedActivityId: String = "moderate",
    val selectedGoalId: String = "maintain",
    val targetWeightKg: Double = 0.0,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

