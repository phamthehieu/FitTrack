package com.example.fittrack.ui.components.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fittrack.ui.theme.FitTrackExtras

private fun parseEmailLocalAndDomain(
    raw: String,
    domainOptions: List<String>,
): Pair<String, String> {
    val trimmed = raw.trim()
    val atIndex = trimmed.lastIndexOf('@')
    val defaultDomain = domainOptions.firstOrNull() ?: "@gmail.com"
    if (atIndex < 0) {
        return "" to defaultDomain
    }
    if (atIndex == 0) {
        val domain = trimmed
        val normalized = domainOptions.firstOrNull { it.equals(domain, ignoreCase = true) } ?: domain
        return "" to normalized
    }
    val local = trimmed.substring(0, atIndex)
    val domain = "@" + trimmed.substring(atIndex + 1)
    val normalized = domainOptions.firstOrNull { it.equals(domain, ignoreCase = true) } ?: domain
    return local to normalized
}

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

    val parsed = parseEmailLocalAndDomain(value, domainOptions)
    val localPart = parsed.first

    // Đuôi vừa chọn trong menu (áp dụng ngay, trước khi `value` từ parent kịp đổi).
    var pendingDomain by remember { mutableStateOf<String?>(null) }
    val selectedDomain = pendingDomain ?: parsed.second

    LaunchedEffect(value, domainOptions) {
        pendingDomain = null
    }

    var domainExpanded by remember { mutableStateOf(false) }
    val latestOnValueChange by rememberUpdatedState(onValueChange)

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
            onExpandedChange = { domainExpanded = it },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(extras.inputBackground)
                    .padding(start = 4.dp, end = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = null,
                    tint = extras.textSecondary,
                    modifier = Modifier.padding(start = 12.dp),
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                ) {
                    if (localPart.isEmpty()) {
                        Text(
                            text = placeholderLocalPart,
                            style = MaterialTheme.typography.bodyLarge,
                            color = extras.inputPlaceholder,
                            modifier = Modifier.align(Alignment.CenterStart),
                        )
                    }
                    BasicTextField(
                        value = localPart,
                        onValueChange = { raw ->
                            val cleaned = raw.replace("@", "").trimStart()
                            val domain = pendingDomain ?: parseEmailLocalAndDomain(value, domainOptions).second
                            latestOnValueChange((cleaned.trim() + domain).trim())
                        },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = domainExpanded,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = scheme.onSurface),
                        singleLine = true,
                        cursorBrush = SolidColor(scheme.primary),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = keyboardActions,
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(scheme.outline.copy(alpha = 0.35f)),
                )

                Row(
                    modifier = Modifier
                        .widthIn(min = 100.dp)
                        .menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                            enabled = domainOptions.isNotEmpty(),
                        )
                        .padding(start = 8.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = selectedDomain,
                        color = scheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = domainExpanded)
                }
            }

            ExposedDropdownMenu(
                expanded = domainExpanded,
                onDismissRequest = { domainExpanded = false },
                modifier = Modifier
                    .exposedDropdownSize(matchAnchorWidth = true)
                    .widthIn(min = 120.dp, max = 220.dp)
                    .background(scheme.surface, RoundedCornerShape(12.dp)),
            ) {
                domainOptions.forEach { option ->
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = option,
                                    color = if (option == selectedDomain) scheme.primary else scheme.onSurface,
                                    fontWeight = if (option == selectedDomain) FontWeight.SemiBold else FontWeight.Normal,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Box(modifier = Modifier.size(16.dp)) {
                                    if (option == selectedDomain) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = scheme.primary,
                                            modifier = Modifier.size(16.dp),
                                        )
                                    }
                                }
                            }
                        },
                        onClick = {
                            pendingDomain = option
                            latestOnValueChange((localPart.trim() + option).trim())
                            domainExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}
