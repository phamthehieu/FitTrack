package com.example.fittrack.ui.screens.onboarding

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.ui.components.inputs.LabeledDropdownField
import com.example.fittrack.ui.components.inputs.LabeledOutlinedTextField
import com.example.fittrack.ui.components.surfaces.FitCardDefaults
import com.example.fittrack.ui.theme.FitTrackExtras
import com.example.fittrack.ui.theme.FitTrackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardingScreen () {
    val colorScheme = MaterialTheme.colorScheme
    val extras = FitTrackExtras.colors
    
    var fullName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var selectedActivity by remember { mutableStateOf("moderate") }

    val activityLevels = listOf(
        Activity("sedentary", "Ít vận động", "Công việc văn phòng", 1.2),
        Activity("light", "Vận động nhẹ", "1-3 ngày/tuần", 1.375),
        Activity("moderate", "Vận động vừa", "3-5 ngày/tuần", 1.55),
        Activity("active", "Vận động nhiều", "Việc nặng", 1.725)
    )

    val bmi = remember(height) {
        val h = height.toFloatOrNull()?.div(100) ?: 0f
        if (h > 0) (70 / (h * h)) else 0f
    }

    // WHO BMI classification + map màu theo mức độ nghiêm trọng
    val (bmiCategory, bmiCategoryColor) = when {
        bmi < 18.5 -> "THIẾU CÂN" to extras.warning
        bmi < 25.0 -> "BÌNH THƯỜNG" to extras.success
        bmi < 30.0 -> "THỪA CÂN" to extras.warning
        bmi < 35.0 -> "BÉO PHÌ ĐỘ I" to colorScheme.error
        bmi < 40.0 -> "BÉO PHÌ ĐỘ II" to colorScheme.error
        else -> "BÉO PHÌ ĐỘ III" to colorScheme.error
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Thiết lập hồ sơ",
                color = colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Nhập thông tin để hệ thống tính toán cho bạn",
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
                    label = "Họ và tên",
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = "Nhập họ và tên",
                    leadingIcon = Icons.Filled.Person
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LabeledOutlinedTextField(
                        modifier = Modifier.weight(1f),
                        label = "Tuổi",
                        value = age,
                        onValueChange = { age = it },
                        placeholder = "",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        LabeledDropdownField(
                            label = "Giới tính",
                            value = gender,
                            onValueChange = { gender = it },
                            options = listOf("Nam", "Nữ", "Other"),
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
                        label = "Chiều cao",
                        value = height,
                        onValueChange = { height = it },
                        placeholder = "",
                        leadingIcon = Icons.Filled.Height
                    )

                    LabeledOutlinedTextField(
                            modifier = Modifier.weight(1f),
                            label = "Cân nặng ",
                            value = weight,
                            onValueChange = { weight = it },
                            placeholder = "",
                            leadingIcon = Icons.Filled.MonitorWeight
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text("MỨC ĐỘ VẬN ĐỘNG", color = extras.textMuted)

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
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = extras.textSecondary)) {
                            append("BMI: ${"%.1f".format(bmi)} (")
                        }
                        withStyle(
                            SpanStyle(
                                color = bmiCategoryColor,
                                fontWeight = FontWeight.SemiBold,
                            )
                        ) {
                            append(bmiCategory)
                        }
                        withStyle(SpanStyle(color = extras.textSecondary)) {
                            append(")")
                        }
                    },
                )

                Spacer(Modifier.height(8.dp))

                LinearProgressIndicator(
                progress = { (bmi / 40).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = bmiCategoryColor,
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
            }
        }

    }
}

@Composable
fun ActivityItem(
    activity: com.example.fittrack.ui.screens.onboarding.Activity,
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

@Preview(showBackground = true, showSystemUi = true,
    device = "spec:width=1080px,height=4400px,dpi=440"
)
@Preview(showBackground = true, showSystemUi = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=1080px,height=4400px,dpi=440"
)
@Composable
private fun OnBoardingScreenPreview() {
    FitTrackTheme {
        OnBoardingScreen()
    }
}