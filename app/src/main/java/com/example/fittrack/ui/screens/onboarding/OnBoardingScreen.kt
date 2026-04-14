package com.example.fittrack.ui.screens.onboarding

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
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.R
import com.example.fittrack.ui.components.inputs.LabeledDropdownField
import com.example.fittrack.ui.components.inputs.LabeledOutlinedTextField
import com.example.fittrack.ui.components.surfaces.FitCardDefaults
import com.example.fittrack.ui.screens.onboarding.model.Goal
import com.example.fittrack.ui.theme.FitTrackExtras
import com.example.fittrack.ui.theme.FitTrackTheme
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardingScreen(
    onFinished: () -> Unit = {},
    onLogoutToLogin: () -> Unit = {},
) {
    val colorScheme = MaterialTheme.colorScheme
    val extras = FitTrackExtras.colors
    val context = LocalContext.current

    BackHandler(enabled = true) {}
    
    var fullName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var selectedActivity by remember { mutableStateOf("moderate") }
    var selectedGoal by remember { mutableStateOf("maintain") }
    var targetWeight by remember { mutableDoubleStateOf(72.0) }

    val activityLevels = listOf(
        com.example.fittrack.ui.screens.onboarding.model.Activity(
            "sedentary",
            stringResource(id = R.string.onboarding_activity_sedentary_label),
            stringResource(id = R.string.onboarding_activity_sedentary_desc),
            1.2
        ),
        com.example.fittrack.ui.screens.onboarding.model.Activity(
            "light",
            stringResource(id = R.string.onboarding_activity_light_label),
            stringResource(id = R.string.onboarding_activity_light_desc),
            1.375
        ),
        com.example.fittrack.ui.screens.onboarding.model.Activity(
            "moderate",
            stringResource(id = R.string.onboarding_activity_moderate_label),
            stringResource(id = R.string.onboarding_activity_moderate_desc),
            1.55
        ),
        com.example.fittrack.ui.screens.onboarding.model.Activity(
            "active",
            stringResource(id = R.string.onboarding_activity_active_label),
            stringResource(id = R.string.onboarding_activity_active_desc),
            1.725
        )
    )

    val bmi = remember(height) {
        val h = height.toFloatOrNull()?.div(100) ?: 0f
        if (h > 0) (70 / (h * h)) else 0f
    }

    val (bmiCategory, bmiCategoryColor) = when {
        bmi < 18.5 -> stringResource(id = R.string.onboarding_bmi_underweight) to extras.warning
        bmi < 25.0 -> stringResource(id = R.string.onboarding_bmi_normal) to extras.success
        bmi < 30.0 -> stringResource(id = R.string.onboarding_bmi_overweight) to extras.warning
        bmi < 35.0 -> stringResource(id = R.string.onboarding_bmi_obese_1) to colorScheme.error
        bmi < 40.0 -> stringResource(id = R.string.onboarding_bmi_obese_2) to colorScheme.error
        else -> stringResource(id = R.string.onboarding_bmi_obese_3) to colorScheme.error
    }

    val tee = remember(age, gender, height, weight, selectedActivity) {
        val w = weight.toFloatOrNull()
        val h = height.toFloatOrNull()
        val a = age.toIntOrNull()
        val activityFactor = activityLevels.firstOrNull { it.id == selectedActivity }?.multiplier ?: 1.55

        if (w == null || h == null || a == null || w <= 0f || h <= 0f || a <= 0) {
            0
        } else {
            val isFemale = gender == context.getString(R.string.onboarding_gender_female)
            val s = if (isFemale) -161 else 5
            val bmr = (10f * w) + (6.25f * h) - (5f * a) + s
            (bmr * activityFactor).toInt().coerceAtLeast(0)
        }
    }

    val numberFormatter = remember {
        DecimalFormat(
            "#,###",
            DecimalFormatSymbols().apply { groupingSeparator = '.' },
        )
    }
    val teeText = if (tee > 0) numberFormatter.format(tee) else "--"

    val idealWeightRangeText = remember(height) {
        val hM = height.toFloatOrNull()?.div(100f) ?: 0f
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
                    value = fullName,
                    onValueChange = { fullName = it },
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
                        value = age,
                        onValueChange = { age = it },
                        placeholder = "",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        LabeledDropdownField(
                            label = stringResource(id = R.string.onboarding_gender_label),
                            value = gender,
                            onValueChange = { gender = it },
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
                        value = height,
                        onValueChange = { height = it },
                        placeholder = "",
                        leadingIcon = Icons.Filled.Height
                    )

                    LabeledOutlinedTextField(
                            modifier = Modifier.weight(1f),
                            label = stringResource(id = R.string.onboarding_weight_label),
                            value = weight,
                            onValueChange = { weight = it },
                            placeholder = "",
                            leadingIcon = Icons.Filled.MonitorWeight
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(stringResource(id = R.string.onboarding_activity_level_title), color = extras.textMuted)

                activityLevels.forEach {
                    ActivityItem(
                        activity = it,
                        selected = selectedActivity == it.id
                    ) {
                        selectedActivity = it.id
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

                goals.forEach {
                    GoalItem(
                        goal = it,
                        selected = selectedGoal == it.id
                    ) {
                        selectedGoal = it.id
                    }
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
                        onClick = { targetWeight = (targetWeight - 0.5).coerceAtLeast(20.0) },
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
                        text = "%.1f".format(targetWeight),
                        color = colorScheme.onSurface,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    IconButton(
                        onClick = { targetWeight = (targetWeight + 0.5).coerceAtMost(250.0) },
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
                onFinished()
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

@Composable
fun ActivityItem(
    activity: com.example.fittrack.ui.screens.onboarding.model.Activity,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val extras = FitTrackExtras.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(
                if (selected) colorScheme.primary else colorScheme.background,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(activity.label, color = if (selected) Color.White else extras.textSecondary)
            Text(activity.desc, color = if (selected) Color.White else extras.textSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
fun GoalItem(goal: Goal, selected: Boolean, onClick: () -> Unit) {

    val colorScheme = MaterialTheme.colorScheme
    val extras = FitTrackExtras.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(
                if (selected) colorScheme.primary else colorScheme.background,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Text(goal.label, color = if (selected) Color.White else extras.textSecondary)
            Text(goal.desc, color = if (selected) Color.White else extras.textSecondary, fontSize = 12.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true,
    device = "spec:width=1080px,height=6400px,dpi=440"
)
@Preview(showBackground = true, showSystemUi = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=1080px,height=6400px,dpi=440"
)
@Composable
private fun OnBoardingScreenPreview() {
    FitTrackTheme {
        OnBoardingScreen()
    }
}