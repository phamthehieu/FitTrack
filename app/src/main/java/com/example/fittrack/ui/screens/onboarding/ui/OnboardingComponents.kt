package com.example.fittrack.ui.screens.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.ui.screens.onboarding.model.Activity
import com.example.fittrack.ui.screens.onboarding.model.Goal
import com.example.fittrack.ui.theme.FitTrackExtras

@Composable
fun OnboardingActivityItem(
    activity: Activity,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val extras = FitTrackExtras.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(
                if (selected) colorScheme.primary else colorScheme.background,
                RoundedCornerShape(12.dp),
            )
            .clickable { onClick() }
            .padding(16.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(activity.label, color = if (selected) Color.White else extras.textSecondary)
            Text(activity.desc, color = if (selected) Color.White else extras.textSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
fun OnboardingGoalItem(
    goal: Goal,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val extras = FitTrackExtras.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.45f)
            .padding(vertical = 6.dp)
            .background(
                if (selected) colorScheme.primary else colorScheme.background,
                RoundedCornerShape(12.dp),
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(16.dp),
    ) {
        Column {
            Text(goal.label, color = if (selected) Color.White else extras.textSecondary)
            Text(goal.desc, color = if (selected) Color.White else extras.textSecondary, fontSize = 12.sp)
        }
    }
}

