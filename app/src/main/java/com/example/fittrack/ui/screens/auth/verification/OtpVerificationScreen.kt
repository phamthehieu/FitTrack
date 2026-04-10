package com.example.fittrack.ui.screens.auth.verification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.ui.theme.FitTrackExtras
import com.example.fittrack.ui.theme.FitTrackTheme
import kotlinx.coroutines.delay

private const val OTP_LENGTH = 6
private const val RESEND_TOTAL_SECONDS = 180 // 3 phút

private fun formatCountdownMmSs(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "%02d:%02d".format(m, s)
}

@Composable
fun OtpVerificationScreen(
    onNavigateBack: () -> Unit = {},
    onOtpComplete: (String) -> Unit = {},
) {
    val colorScheme = MaterialTheme.colorScheme
    val extras = FitTrackExtras.colors
    val haptics = LocalHapticFeedback.current

    var otp by remember { mutableStateOf("") }
    var timerToken by remember { mutableIntStateOf(0) }
    var secondsLeft by remember { mutableIntStateOf(RESEND_TOTAL_SECONDS) }

    LaunchedEffect(timerToken) {
        while (secondsLeft > 0) {
            delay(1_000)
            secondsLeft--
        }
    }

    val canResend = secondsLeft == 0
    val timerLabelColor = extras.success

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = colorScheme.onSurface,
                )
            }
            Text(
                text = "VERIFICATION",
                modifier = Modifier.weight(1f),
                color = colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.width(48.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Xác thực mã OTP",
                color = colorScheme.onSurface,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Vui lòng nhập mã 6 chữ số đã được gửi tới số điện thoại của bạn",
                color = extras.textMuted,
                fontSize = 14.sp,
                lineHeight = 22.sp,
            )

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                repeat(OTP_LENGTH) { index ->
                    OtpDigitCell(
                        digit = otp.getOrNull(index),
                        isFilled = index < otp.length,
                        modifier = Modifier.weight(1f),
                        placeholderMuted = extras.textMuted,
                        filledColor = colorScheme.primary,
                        boxBackground = extras.inputBackground,
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(colorScheme.surface.copy(alpha = 0.35f))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = timerLabelColor,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = "Gửi lại mã sau ${formatCountdownMmSs(secondsLeft)}",
                    color = timerLabelColor,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Bạn chưa nhận được mã? ",
                    color = extras.textMuted,
                    fontSize = 14.sp,
                )
                Text(
                    text = "Gửi lại",
                    color = if (canResend) colorScheme.primary else extras.textMuted.copy(alpha = 0.45f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(
                        enabled = canResend,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        otp = ""
                        secondsLeft = RESEND_TOTAL_SECONDS
                        timerToken++
                    },
                )
            }

            Spacer(Modifier.weight(1f))
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = colorScheme.surfaceVariant.copy(alpha = 0.45f),
            tonalElevation = 0.dp,
        ) {
            OtpNumericKeypad(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                onDigit = { d ->
                    if (otp.length < OTP_LENGTH) {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        otp += d.toString()
                        if (otp.length == OTP_LENGTH) {
                            onOtpComplete(otp)
                        }
                    }
                },
                onBackspace = {
                    if (otp.isNotEmpty()) {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        otp = otp.dropLast(1)
                    }
                },
                onDot = { /* không dùng cho OTP */ },
                keyColor = colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun OtpDigitCell(
    digit: Char?,
    isFilled: Boolean,
    modifier: Modifier = Modifier,
    placeholderMuted: Color,
    filledColor: Color,
    boxBackground: Color,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(boxBackground),
        contentAlignment = Alignment.Center,
    ) {
        val charToShow = if (isFilled && digit != null) digit else '0'
        Text(
            text = charToShow.toString(),
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isFilled && digit != null) filledColor else placeholderMuted.copy(alpha = 0.35f),
        )
    }
}

@Composable
private fun OtpNumericKeypad(
    modifier: Modifier = Modifier,
    onDigit: (Int) -> Unit,
    onBackspace: () -> Unit,
    onDot: () -> Unit,
    keyColor: Color,
) {
    val rowModifier = Modifier
        .fillMaxWidth()
        .height(52.dp)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            listOf(1, 2, 3),
            listOf(4, 5, 6),
            listOf(7, 8, 9),
        ).forEach { row ->
            Row(
                modifier = rowModifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { num ->
                    KeypadKey(
                        modifier = Modifier.weight(1f),
                        onClick = { onDigit(num) },
                        content = {
                            Text(
                                text = num.toString(),
                                color = keyColor,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        },
                    )
                }
            }
        }

        Row(
            modifier = rowModifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            KeypadKey(
                modifier = Modifier.weight(1f),
                onClick = onDot,
                content = {
                    Text(
                        text = ".",
                        color = keyColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                    )
                },
            )
            KeypadKey(
                modifier = Modifier.weight(1f),
                onClick = { onDigit(0) },
                content = {
                    Text(
                        text = "0",
                        color = keyColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                    )
                },
            )
            KeypadKey(
                modifier = Modifier.weight(1f),
                onClick = onBackspace,
                content = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Xóa",
                        tint = keyColor,
                        modifier = Modifier.size(22.dp),
                    )
                },
            )
        }
    }
}

@Composable
private fun KeypadKey(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OtpVerificationPreview() {
    FitTrackTheme {
        OtpVerificationScreen()
    }
}
