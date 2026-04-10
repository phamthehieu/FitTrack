package com.example.fittrack.ui.components.inputs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.fittrack.ui.theme.FitTrackExtras

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EmailDomainField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    domainOptions: List<String> = listOf("@gmail.com", "@yahoo.com", "@outlook.com"),
    modifier: Modifier = Modifier,
    placeholderLocalPart: String = "tenban",
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val scheme = MaterialTheme.colorScheme
    val extras = FitTrackExtras.colors

    val (initialLocal, initialDomain) = remember(value, domainOptions) {
        val trimmed = value.trim()
        val atIndex = trimmed.lastIndexOf('@')
        if (atIndex <= 0) {
            "" to (domainOptions.firstOrNull() ?: "@gmail.com")
        } else {
            val local = trimmed.substring(0, atIndex)
            val domain = "@" + trimmed.substring(atIndex + 1)
            local to (domainOptions.firstOrNull { it.equals(domain, ignoreCase = true) } ?: domain)
        }
    }

    var localPart by remember { mutableStateOf(initialLocal) }
    var selectedDomain by remember { mutableStateOf(initialDomain) }
    var domainExpanded by remember { mutableStateOf(false) }

    // Nếu parent set value khác (ví dụ prefill), đồng bộ lại state.
    // (Đơn giản: chỉ sync khi localPart rỗng để tránh giật khi user đang gõ)
    if (value.isNotBlank() && localPart.isBlank()) {
        localPart = initialLocal
        selectedDomain = initialDomain
    }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = extras.textMuted,
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = domainExpanded,
            onExpandedChange = { domainExpanded = !domainExpanded },
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = localPart,
                onValueChange = { raw ->
                    val cleaned = raw.replace("@", "").trimStart()
                    localPart = cleaned
                    onValueChange((cleaned.trim() + selectedDomain).trim())
                },
                placeholder = { Text(text = placeholderLocalPart, color = extras.inputPlaceholder) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = null,
                        tint = extras.textSecondary,
                    )
                },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(enabled = domainOptions.isNotEmpty()) {
                                domainExpanded = !domainExpanded
                            }
                            .padding(start = 8.dp),
                    ) {
                        Text(
                            text = selectedDomain,
                            color = scheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            tint = extras.textSecondary,
                            modifier = Modifier.padding(start = 2.dp),
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = keyboardActions,
                shape = RoundedCornerShape(percent = 50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = scheme.onSurface,
                    unfocusedTextColor = scheme.onSurface,
                    focusedContainerColor = extras.inputBackground,
                    unfocusedContainerColor = extras.inputBackground,
                    cursorColor = scheme.primary,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    errorBorderColor = scheme.error,
                    focusedLeadingIconColor = extras.textSecondary,
                    unfocusedLeadingIconColor = extras.textSecondary,
                    focusedTrailingIconColor = extras.textSecondary,
                    unfocusedTrailingIconColor = extras.textSecondary,
                    focusedPlaceholderColor = extras.inputPlaceholder,
                    unfocusedPlaceholderColor = extras.inputPlaceholder,
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
            )

            ExposedDropdownMenu(
                expanded = domainExpanded,
                onDismissRequest = { domainExpanded = false },
                modifier = Modifier.background(scheme.surface),
            ) {
                domainOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = {
                            selectedDomain = option
                            onValueChange((localPart.trim() + option).trim())
                            domainExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}

