package com.example.fittrack.ui.screens.onboarding

import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fittrack.R
import com.example.fittrack.ui.components.inputs.LabeledDropdownField
import com.example.fittrack.ui.components.inputs.LabeledOutlinedTextField
import com.example.fittrack.ui.components.surfaces.FitCardDefaults
import com.example.fittrack.ui.screens.onboarding.model.Activity
import com.example.fittrack.ui.screens.onboarding.model.Goal
import com.example.fittrack.ui.screens.onboarding.ui.OnboardingActivityItem
import com.example.fittrack.ui.screens.onboarding.ui.OnboardingGoalItem
import com.example.fittrack.ui.theme.FitTrackExtras
import com.example.fittrack.ui.theme.FitTrackTheme
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

private fun isLayoutPreviewContext(applicationContext: android.content.Context): Boolean {
    val n = applicationContext.javaClass.name
    return n.startsWith("com.android.layout") || n.contains("layout.bridge")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardingScreen(
    onFinished: () -> Unit = {},
    onLogoutToLogin: () -> Unit = {},
) {
    val context = LocalContext.current
    val applicationContext = context.applicationContext
    if (LocalInspectionMode.current ||
        LocalView.current.isInEditMode ||
        isLayoutPreviewContext(applicationContext)
    ) {
        OnBoardingScreenPreviewBody(onLogoutToLogin = onLogoutToLogin)
        return
    }

    val app = applicationContext as? Application

    if (app != null) {
        val vmFactory = remember(app) { OnboardingViewModel.Factory(app) }
        val vm: OnboardingViewModel = viewModel(factory = vmFactory)
        val state by vm.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(vm) {
            vm.events.collect { event ->
                when (event) {
                    OnboardingViewModel.Event.Finished -> onFinished()
                }
            }
        }

        OnBoardingScreenContent(
            state = state,
            onAction = vm::onAction,
            computeBmi = vm::computeBmi,
            computeTee = vm::computeTdee,
            onLogoutToLogin = onLogoutToLogin,
        )
        return
    }

    OnBoardingScreenPreviewBody(onLogoutToLogin = onLogoutToLogin)
}

@Composable
internal fun OnBoardingScreenPreviewBody(onLogoutToLogin: () -> Unit) {
    val context = LocalContext.current
    val maleLabel = stringResource(R.string.onboarding_gender_male)
    var state by remember(maleLabel) {
        mutableStateOf(
            OnboardingUiState(
                fullName = "Nguyễn Văn A",
                age = "30",
                genderLabel = maleLabel,
                heightCmText = "170",
                weightKgText = "70",
                selectedActivityId = "moderate",
                selectedGoalId = "maintain",
                targetWeightKg = 0.0,
            ).withGoalTargetFromMetrics(),
        )
    }
    val onAction: (OnboardingAction) -> Unit = { action ->
        when (action) {
            OnboardingAction.SubmitClicked -> Unit
            else -> {
                var next = state.withAction(action)
                if (action is OnboardingAction.HeightChanged ||
                    action is OnboardingAction.WeightChanged ||
                    action is OnboardingAction.GoalChanged
                ) {
                    next = next.withGoalTargetFromMetrics()
                }
                state = next
            }
        }
    }
    OnBoardingScreenContent(
        state = state,
        onAction = onAction,
        computeBmi = { h -> computeOnboardingBmi(h, state.weightKgText) },
        computeTee = { a, g, h, w, id ->
            computeOnboardingTdee(context, a, g, h, w, id)
        },
        onLogoutToLogin = onLogoutToLogin,
    )
}

@Composable
private fun OnBoardingScreenContent(
    state: OnboardingUiState,
    onAction: (OnboardingAction) -> Unit,
    computeBmi: (heightCmText: String) -> Float,
    computeTee: (
        ageText: String,
        genderLabel: String,
        heightCmText: String,
        weightKgText: String,
        selectedActivityId: String,
    ) -> Int,
    onLogoutToLogin: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val extras = FitTrackExtras.colors
    val context = LocalContext.current

    BackHandler(enabled = true) {}

    val activityLevels = listOf(
        Activity(
            "sedentary",
            stringResource(id = R.string.onboarding_activity_sedentary_label),
            stringResource(id = R.string.onboarding_activity_sedentary_desc),
            1.2
        ),
        Activity(
            "light",
            stringResource(id = R.string.onboarding_activity_light_label),
            stringResource(id = R.string.onboarding_activity_light_desc),
            1.375
        ),
        Activity(
            "moderate",
            stringResource(id = R.string.onboarding_activity_moderate_label),
            stringResource(id = R.string.onboarding_activity_moderate_desc),
            1.55
        ),
        Activity(
            "active",
            stringResource(id = R.string.onboarding_activity_active_label),
            stringResource(id = R.string.onboarding_activity_active_desc),
            1.725
        )
    )

    val bmi = computeBmi(state.heightCmText)

    val (bmiCategory, bmiCategoryColor) = when {
        bmi < 18.5 -> stringResource(id = R.string.onboarding_bmi_underweight) to extras.warning
        bmi < 25.0 -> stringResource(id = R.string.onboarding_bmi_normal) to extras.success
        bmi < 30.0 -> stringResource(id = R.string.onboarding_bmi_overweight) to extras.warning
        bmi < 35.0 -> stringResource(id = R.string.onboarding_bmi_obese_1) to colorScheme.error
        bmi < 40.0 -> stringResource(id = R.string.onboarding_bmi_obese_2) to colorScheme.error
        else -> stringResource(id = R.string.onboarding_bmi_obese_3) to colorScheme.error
    }

    val tee = computeTee(
        state.age,
        state.genderLabel,
        state.heightCmText,
        state.weightKgText,
        state.selectedActivityId,
    )

    val numberFormatter = remember {
        DecimalFormat(
            "#,###",
            DecimalFormatSymbols().apply { groupingSeparator = '.' },
        )
    }
    val teeText = if (tee > 0) numberFormatter.format(tee) else "--"

    val idealWeightRangeText = remember(state.heightCmText) {
        val hCm = state.heightCmText.trim().replace(',', '.').replace(" ", "").toFloatOrNull() ?: 0f
        val hM = hCm / 100f
        if (hM <= 0f) {
            context.getString(R.string.onboarding_ideal_weight_unknown)
        } else {
            val minKg = 18.5f * (hM * hM)
            val maxKg = 24.9f * (hM * hM)
            val minStr = "%.0f".format(minKg)
            val maxStr = "%.0f".format(maxKg)
            context.getString(R.string.onboarding_ideal_weight_range, minStr, maxStr)
        }
    }

    val goals = listOf(
        Goal(
            "lose",
            stringResource(id = R.string.onboarding_goal_lose_label),
            stringResource(id = R.string.onboarding_goal_lose_desc),
        ),
        Goal(
            "maintain",
            stringResource(id = R.string.onboarding_goal_maintain_label),
            stringResource(id = R.string.onboarding_goal_maintain_desc),
        ),
        Goal(
            "gain",
            stringResource(id = R.string.onboarding_goal_gain_label),
            stringResource(id = R.string.onboarding_goal_gain_desc),
        ),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = onLogoutToLogin) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.onboarding_logout),
                    tint = colorScheme.primary,
                )
            }

            Text(
                stringResource(id = R.string.onboarding_title),
                color = colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp)

            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            stringResource(id = R.string.onboarding_subtitle),
            color = extras.textMuted,
            fontSize = 13.sp,
            lineHeight = 24.sp
        )

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = FitCardDefaults.containerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = BorderStroke(1.dp, FitCardDefaults.borderColor),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                LabeledOutlinedTextField(
                    label = stringResource(id = R.string.onboarding_full_name_label),
                    value = state.fullName,
                    onValueChange = { onAction(OnboardingAction.FullNameChanged(it)) },
                    placeholder = stringResource(id = R.string.onboarding_full_name_placeholder),
                    leadingIcon = Icons.Filled.Person
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LabeledOutlinedTextField(
                        modifier = Modifier.weight(1f),
                        label = stringResource(id = R.string.onboarding_age_label),
                        value = state.age,
                        onValueChange = {
                            val digitsOnly = it.filter(Char::isDigit).take(3)
                            onAction(OnboardingAction.AgeChanged(digitsOnly))
                        },
                        placeholder = "",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        LabeledDropdownField(
                            label = stringResource(id = R.string.onboarding_gender_label),
                            value = state.genderLabel,
                            onValueChange = { onAction(OnboardingAction.GenderChanged(it)) },
                            options = listOf(
                                stringResource(id = R.string.onboarding_gender_male),
                                stringResource(id = R.string.onboarding_gender_female),
                                stringResource(id = R.string.onboarding_gender_other),
                            ),
                            placeholder = "",
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = FitCardDefaults.containerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = BorderStroke(1.dp, FitCardDefaults.borderColor),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LabeledOutlinedTextField(
                        modifier = Modifier.weight(1f),
                        label = stringResource(id = R.string.onboarding_height_label),
                        value = state.heightCmText,
                        onValueChange = { onAction(OnboardingAction.HeightChanged(it)) },
                        placeholder = "",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = Icons.Filled.Height,
                        suffix = {
                            Text(
                                text = stringResource(R.string.onboarding_height_unit_suffix),
                                style = MaterialTheme.typography.bodyLarge,
                                color = extras.textMuted,
                            )
                        },
                    )

                    LabeledOutlinedTextField(
                        modifier = Modifier.weight(1f),
                        label = stringResource(id = R.string.onboarding_weight_label),
                        value = state.weightKgText,
                        onValueChange = { onAction(OnboardingAction.WeightChanged(it)) },
                        placeholder = "",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = Icons.Filled.MonitorWeight,
                        suffix = {
                            Text(
                                text = stringResource(R.string.onboarding_weight_unit_suffix),
                                style = MaterialTheme.typography.bodyLarge,
                                color = extras.textMuted,
                            )
                        },

                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(stringResource(id = R.string.onboarding_activity_level_title), color = extras.textMuted)

                activityLevels.forEach {
                    OnboardingActivityItem(
                        activity = it,
                        selected = state.selectedActivityId == it.id,
                        onClick = { onAction(OnboardingAction.ActivityChanged(it.id)) },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = FitCardDefaults.containerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = BorderStroke(1.dp, FitCardDefaults.borderColor),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.onboarding_bmi_title),
                    color = extras.textMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.6.sp,
                )

                Spacer(Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "%.1f".format(bmi),
                        color = colorScheme.onSurface,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 44.sp,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.onboarding_bmi_unit),
                        color = extras.textMuted,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(bmiCategoryColor.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(bmiCategoryColor, CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = bmiCategory,
                        color = bmiCategoryColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Spacer(Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { (bmi / 40f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(percent = 50)),
                    color = bmiCategoryColor,
                    trackColor = FitCardDefaults.borderColor.copy(alpha = 0.9f),
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )

                Spacer(Modifier.height(18.dp))

                Text(
                    text = stringResource(id = R.string.onboarding_tdee_title),
                    color = extras.textMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.6.sp,
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = extras.success,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        ) { append(teeText) }
                        append(" ")
                        withStyle(SpanStyle(color = extras.textMuted, fontSize = 14.sp)) {
                            append(stringResource(id = R.string.onboarding_tdee_unit))
                        }
                    },
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = stringResource(id = R.string.onboarding_tdee_description),
                    color = extras.textMuted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                )

            }
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = FitCardDefaults.containerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = BorderStroke(1.dp, FitCardDefaults.borderColor),
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    stringResource(id = R.string.onboarding_goal_title),
                    color = extras.textSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                val loseGoalDisabled = bmi > 0f && bmi < 18.5f
                val gainGoalDisabled = bmi > 0f && bmi >= 25f
                goals.forEach {
                    OnboardingGoalItem(
                        goal = it,
                        selected = state.selectedGoalId == it.id,
                        enabled = when (it.id) {
                            "lose" -> !loseGoalDisabled
                            "gain" -> !gainGoalDisabled
                            else -> true
                        },
                        onClick = { onAction(OnboardingAction.GoalChanged(it.id)) },
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = FitCardDefaults.containerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = BorderStroke(1.dp, FitCardDefaults.borderColor),
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_target_weight_title),
                    color = extras.textMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.6.sp,
                )
                Text(
                    text = idealWeightRangeText,
                    color = extras.textMuted,
                    fontSize = 12.sp,
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(colorScheme.surfaceVariant.copy(alpha = 0.18f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(
                        onClick = {
                            onAction(
                                OnboardingAction.TargetWeightChanged(
                                    (state.targetWeightKg - 0.5).coerceAtLeast(20.0)
                                )
                            )
                        },
                        enabled = state.targetWeightKg > 0.0,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(extras.inputBackground),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = stringResource(id = R.string.onboarding_target_weight_decrease),
                            tint = extras.textSecondary,
                        )
                    }

                    Text(
                        text = if (state.targetWeightKg > 0.0) {
                            "%.1f".format(state.targetWeightKg)
                        } else {
                            "—"
                        },
                        color = colorScheme.onSurface,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    IconButton(
                        onClick = {
                            onAction(
                                OnboardingAction.TargetWeightChanged(
                                    (state.targetWeightKg + 0.5).coerceAtMost(250.0)
                                )
                            )
                        },
                        enabled = state.targetWeightKg > 0.0,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(extras.inputBackground),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(id = R.string.onboarding_target_weight_increase),
                            tint = extras.textSecondary,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                onAction(OnboardingAction.SubmitClicked)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(percent = 50),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = Color.White,
            ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_start_button),
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.6.sp,
                )
                Spacer(Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                )
            }
        }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=1080px,height=6400px,dpi=440",
)
@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=1080px,height=6400px,dpi=440",
)
@Composable
private fun OnBoardingScreenPreview() {
    FitTrackTheme {
        OnBoardingScreenPreviewBody(onLogoutToLogin = {})
    }
}