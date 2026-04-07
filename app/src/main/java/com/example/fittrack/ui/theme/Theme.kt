package com.example.fittrack.ui.theme

import android.app.Activity
import android.content.ContextWrapper
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = FitPrimaryDark,
    onPrimary = FitOnPrimaryDark,
    secondary = FitPrimaryDarkVariant,
    onSecondary = FitOnPrimaryDark,
    tertiary = FitWarningDark,
    onTertiary = FitOnPrimaryDark,

    background = FitBackgroundDark,
    onBackground = FitTextDark,
    surface = FitSurfaceDark,
    onSurface = FitTextDark,
    surfaceVariant = FitSurfaceElevatedDark,
    onSurfaceVariant = FitTextSecondaryDark,
    outline = FitBorderDark,

    error = FitErrorDark,
    onError = FitOnPrimaryDark,
)

private val LightColorScheme = lightColorScheme(
    primary = FitPrimaryLight,
    onPrimary = FitOnPrimaryLight,
    secondary = FitPrimaryLightVariant,
    onSecondary = FitOnPrimaryLight,
    tertiary = FitWarningLight,
    onTertiary = FitOnPrimaryLight,

    background = FitBackgroundLight,
    onBackground = FitTextLight,
    surface = FitSurfaceLight,
    onSurface = FitTextLight,
    surfaceVariant = FitBorderLightVariant,
    onSurfaceVariant = FitTextSecondaryLight,
    outline = FitBorderLight,

    error = FitErrorLight,
    onError = FitOnPrimaryLight,
)

@Composable
fun FitTrackTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    SideEffect {
        if (!view.isInEditMode) {
            val window = view.findActivity()?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)
            // false = icon/giờ/pin sáng (trắng) trên nền tối; true = icon tối trên nền sáng
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    val extraColors = if (darkTheme) {
        FitTrackExtraColors(
            textSecondary = FitTextSecondaryDark,
            textMuted = FitTextMutedDark,
            borderLight = FitBorderDarkVariant,
            inputBackground = FitInputBackgroundDark,
            inputPlaceholder = FitInputPlaceholderDark,
            success = FitSuccessDark,
            warning = FitWarningDark,
            shadow = FitShadowDark,
            overlay = FitOverlayDark,
        )
    } else {
        FitTrackExtraColors(
            textSecondary = FitTextSecondaryLight,
            textMuted = FitTextMutedLight,
            borderLight = FitBorderLightVariant,
            inputBackground = FitInputBackgroundLight,
            inputPlaceholder = FitInputPlaceholderLight,
            success = FitSuccessLight,
            warning = FitWarningLight,
            shadow = FitShadowLight,
            overlay = FitOverlayLight,
        )
    }

    CompositionLocalProvider(
        LocalFitTrackExtraColors provides extraColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

private fun android.view.View.findActivity(): Activity? = context.findActivityFromContext()

private fun android.content.Context.findActivityFromContext(): Activity? {
    var ctx: android.content.Context? = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return ctx as? Activity
}