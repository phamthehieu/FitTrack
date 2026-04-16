package com.example.fittrack.ui.screens.onboarding

sealed interface OnboardingAction {
    data class FullNameChanged(val value: String) : OnboardingAction
    data class AgeChanged(val value: String) : OnboardingAction
    data class GenderChanged(val value: String) : OnboardingAction
    data class HeightChanged(val value: String) : OnboardingAction
    data class WeightChanged(val value: String) : OnboardingAction
    data class ActivityChanged(val id: String) : OnboardingAction
    data class GoalChanged(val id: String) : OnboardingAction
    data class TargetWeightChanged(val value: Double) : OnboardingAction

    data object SubmitClicked : OnboardingAction
}

