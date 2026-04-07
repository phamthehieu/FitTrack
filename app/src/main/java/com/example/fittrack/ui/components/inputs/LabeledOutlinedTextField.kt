package com.example.fittrack.ui.components.inputs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import androidx.compose.runtime.Immutable
import com.example.fittrack.ui.theme.FitTrackExtras

@Immutable
data class LabeledOutlinedTextFieldColors(
    val container: Color,
    val placeholder: Color,
    val icon: Color,
    val label: Color,
)

@Composable
fun LabeledOutlinedTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector? = null,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier.fillMaxWidth(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    colors: LabeledOutlinedTextFieldColors = run {
        val extras = FitTrackExtras.colors
        LabeledOutlinedTextFieldColors(
            container = extras.inputBackground,
            placeholder = extras.inputPlaceholder,
            icon = extras.textSecondary,
            label = extras.textMuted,
        )
    },
) {
    val scheme = MaterialTheme.colorScheme

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.label,
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = colors.placeholder) },
            leadingIcon = leadingIcon?.let { icon ->
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colors.icon,
                    )
                }
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = RoundedCornerShape(percent = 50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = scheme.onSurface,
                unfocusedTextColor = scheme.onSurface,
                focusedContainerColor = colors.container,
                unfocusedContainerColor = colors.container,
                cursorColor = scheme.primary,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = scheme.error,
                focusedLeadingIconColor = colors.icon,
                unfocusedLeadingIconColor = colors.icon,
                focusedPlaceholderColor = colors.placeholder,
                unfocusedPlaceholderColor = colors.placeholder,
            ),
            modifier = textFieldModifier.padding(top = 8.dp),
            singleLine = singleLine,
        )
    }
}

