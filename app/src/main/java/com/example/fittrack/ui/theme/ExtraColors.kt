package com.example.fittrack.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class FitTrackExtraColors(
    val textSecondary: Color,
    val textMuted: Color,
    val borderLight: Color,
    val inputBackground: Color,
    val inputPlaceholder: Color,
    val success: Color,
    val warning: Color,
    val shadow: Color,
    val overlay: Color,
)

val LocalFitTrackExtraColors = staticCompositionLocalOf {
    FitTrackExtraColors(
        textSecondary = Color.Unspecified,
        textMuted = Color.Unspecified,
        borderLight = Color.Unspecified,
        inputBackground = Color.Unspecified,
        inputPlaceholder = Color.Unspecified,
        success = Color.Unspecified,
        warning = Color.Unspecified,
        shadow = Color.Unspecified,
        overlay = Color.Unspecified,
    )
}

object FitTrackExtras {
    val colors: FitTrackExtraColors
        @Composable
        @ReadOnlyComposable
        get() = LocalFitTrackExtraColors.current
}
