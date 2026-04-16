package com.example.fittrack.ui.screens.onboarding

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fittrack.R
import com.example.fittrack.data.local.db.DatabaseProvider
import com.example.fittrack.data.local.db.entities.DailySummaryEntity
import com.example.fittrack.data.local.db.entities.UserProfileEntity
import com.example.fittrack.data.local.db.entities.WeightLogEntity
import com.example.fittrack.data.repository.FitTrackRepository
import com.example.fittrack.domain.calculators.BmiCalculator
import com.example.fittrack.domain.calculators.TdeeCalculator
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

internal fun OnboardingUiState.withAction(action: OnboardingAction): OnboardingUiState {
    return when (action) {
        is OnboardingAction.FullNameChanged -> copy(fullName = action.value, errorMessage = null)
        is OnboardingAction.AgeChanged -> copy(age = action.value, errorMessage = null)
        is OnboardingAction.GenderChanged -> copy(genderLabel = action.value, errorMessage = null)
        is OnboardingAction.HeightChanged -> copy(heightCmText = action.value, errorMessage = null)
        is OnboardingAction.WeightChanged -> copy(weightKgText = action.value, errorMessage = null)
        is OnboardingAction.ActivityChanged -> copy(selectedActivityId = action.id, errorMessage = null)
        is OnboardingAction.GoalChanged -> copy(selectedGoalId = action.id, errorMessage = null)
        is OnboardingAction.TargetWeightChanged -> copy(targetWeightKg = action.value, errorMessage = null)
        OnboardingAction.SubmitClicked -> this
    }
}

private fun String.parseFloatLocaleOrNull(): Float? =
    trim().replace(',', '.').replace(" ", "").toFloatOrNull()

private fun String.parseIntLocaleOrNull(): Int? =
    trim().replace(" ", "").toIntOrNull()

internal fun computeOnboardingBmi(heightCmText: String, weightKgText: String): Float {
    val heightCm = heightCmText.parseFloatLocaleOrNull() ?: 0f
    val weightKg = weightKgText.parseFloatLocaleOrNull() ?: 0f
    return BmiCalculator.calculate(heightCm = heightCm, weightKg = weightKg)
}

internal fun computeOnboardingTdee(
    context: Context,
    ageText: String,
    genderLabel: String,
    heightCmText: String,
    weightKgText: String,
    selectedActivityId: String,
): Int {
    val w = weightKgText.parseFloatLocaleOrNull()
    val h = heightCmText.parseFloatLocaleOrNull()
    val a = ageText.parseIntLocaleOrNull()
    val activityFactor = when (selectedActivityId) {
        "sedentary" -> 1.2
        "light" -> 1.375
        "moderate" -> 1.55
        "active" -> 1.725
        else -> 1.55
    }

    if (w == null || h == null || a == null || w <= 0f || h <= 0f || a <= 0) return 0

    val isFemale = genderLabel == context.getString(R.string.onboarding_gender_female)
    return TdeeCalculator.calculateMifflinStJeor(
        weightKg = w,
        heightCm = h,
        ageYears = a,
        isFemale = isFemale,
        activityMultiplier = activityFactor,
    )
}

/**
 * Căn mục tiêu (giảm/tăng/giữ) và cân mục tiêu theo BMI + khoảng cân khỏe mạnh (BMI 18,5–24,9).
 * Gọi khi đổi chiều cao, cân nặng hoặc loại mục tiêu — không gọi khi người dùng chỉnh ± cân mục tiêu.
 */
internal fun OnboardingUiState.withGoalTargetFromMetrics(): OnboardingUiState {
    val hCm = heightCmText.parseFloatLocaleOrNull() ?: return this
    if (hCm <= 0f) return this

    val hM = hCm / 100.0
    val minKg = 18.5 * hM * hM
    val maxKg = 24.9 * hM * hM
    val midKg = (minKg + maxKg) / 2.0

    val w = weightKgText.parseFloatLocaleOrNull()?.toDouble()
    val bmi = if (w != null && w > 0.0) w / (hM * hM) else 0.0

    var g = selectedGoalId
    if (bmi > 0.0) {
        if (bmi >= 25.0 && g == "gain") g = "maintain"
        if (bmi < 18.5 && g == "lose") g = "maintain"
    }

    val targetKg = when (g) {
        "lose" -> {
            val cw = w ?: midKg
            min(midKg, cw - 0.1).coerceIn(minKg, maxKg)
        }
        "gain" -> {
            val cw = w ?: midKg
            max(midKg, cw + 0.1).coerceIn(minKg, maxKg)
        }
        else -> when {
            w == null -> midKg
            w in minKg..maxKg -> w
            w < minKg -> minKg
            else -> maxKg
        }
    }

    val rounded = (round(targetKg * 10.0) / 10.0).coerceIn(20.0, 250.0)
    return copy(selectedGoalId = g, targetWeightKg = rounded)
}

class OnboardingViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = FitTrackRepository(DatabaseProvider.get(app.applicationContext))

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    fun onAction(action: OnboardingAction) {
        when (action) {
            OnboardingAction.SubmitClicked -> submit()
            else -> _uiState.update { current ->
                val next = current.withAction(action)
                when (action) {
                    is OnboardingAction.HeightChanged,
                    is OnboardingAction.WeightChanged,
                    is OnboardingAction.GoalChanged,
                    -> next.withGoalTargetFromMetrics()
                    else -> next
                }
            }
        }
    }

    fun computeBmi(heightCmText: String): Float {
        return computeOnboardingBmi(heightCmText, _uiState.value.weightKgText)
    }

    fun computeTdee(
        ageText: String,
        genderLabel: String,
        heightCmText: String,
        weightKgText: String,
        selectedActivityId: String,
    ): Int {
        return computeOnboardingTdee(
            getApplication(),
            ageText,
            genderLabel,
            heightCmText,
            weightKgText,
            selectedActivityId,
        )
    }

    private fun submit() {
        val current = _uiState.value
        if (current.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val ctx = getApplication<Application>()
                val heightCm = current.heightCmText.toDoubleOrNull()
                val startWeightKg = current.weightKgText.toDoubleOrNull()
                val normalizedGender = when (current.genderLabel) {
                    ctx.getString(R.string.onboarding_gender_female) -> "female"
                    ctx.getString(R.string.onboarding_gender_male) -> "male"
                    else -> "other"
                }
                val activityLevel = when (current.selectedActivityId) {
                    "sedentary" -> 1
                    "light" -> 2
                    "moderate" -> 3
                    "active" -> 4
                    else -> 3
                }

                val tdee = computeTdee(
                    ageText = current.age,
                    genderLabel = current.genderLabel,
                    heightCmText = current.heightCmText,
                    weightKgText = current.weightKgText,
                    selectedActivityId = current.selectedActivityId,
                ).takeIf { it > 0 }?.toDouble()

                val dailyTarget = when (current.selectedGoalId) {
                    "lose" -> (tdee?.minus(500.0))
                    "gain" -> (tdee?.plus(300.0))
                    else -> tdee
                }?.coerceAtLeast(1200.0)

                val targetKg = when {
                    current.targetWeightKg > 0.0 -> current.targetWeightKg
                    else -> current.withGoalTargetFromMetrics().targetWeightKg
                }

                val userId = repo.upsertUserProfile(
                    UserProfileEntity(
                        name = current.fullName.trim().ifBlank { "User" },
                        gender = normalizedGender,
                        birthDate = null, // hiện UI nhập tuổi, chưa có ngày sinh
                        heightCm = heightCm,
                        startWeight = startWeightKg,
                        targetWeight = targetKg,
                        goalType = current.selectedGoalId,
                        activityLevel = activityLevel,
                        tdee = tdee,
                        dailyCaloTarget = dailyTarget,
                    )
                )

                // Seed dữ liệu tối thiểu cho ngày đầu: cân nặng + summary hôm nay
                val today = LocalDate.now()
                if (startWeightKg != null && startWeightKg > 0.0) {
                    val bmi = if (heightCm != null && heightCm > 0.0) {
                        BmiCalculator.calculate(
                            heightCm = heightCm.toFloat(),
                            weightKg = startWeightKg.toFloat(),
                        ).takeIf { it > 0f }?.toDouble()
                    } else null

                    repo.upsertWeightLog(
                        WeightLogEntity(
                            userId = userId,
                            logDate = today,
                            weightKg = startWeightKg,
                            bmi = bmi,
                            note = null,
                        )
                    )
                }

                repo.upsertDailySummary(
                    DailySummaryEntity(
                        userId = userId,
                        summaryDate = today,
                        totalCalories = 0.0,
                        totalProtein = 0.0,
                        totalCarb = 0.0,
                        totalFat = 0.0,
                        weightKg = startWeightKg,
                        caloDeficit = 0.0,
                    )
                )

                _events.tryEmit(Event.Finished)
            } catch (t: Throwable) {
                _uiState.update { it.copy(errorMessage = t.message ?: "Lưu dữ liệu thất bại") }
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    sealed interface Event {
        data object Finished : Event
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
                return OnboardingViewModel(app) as T
            }
            error("Unknown ViewModel class: $modelClass")
        }
    }
}

