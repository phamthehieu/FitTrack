package com.example.fittrack.ui.components.surfaces

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.example.fittrack.ui.theme.FitTrackExtras

object FitCardDefaults {

    val containerColor: Color
        @Composable
        @ReadOnlyComposable
        get() {
            val scheme = MaterialTheme.colorScheme
            val extras = FitTrackExtras.colors
            return if (scheme.surface == extras.inputBackground) scheme.surfaceVariant else scheme.surface
        }

    val borderColor: Color
        @Composable
        @ReadOnlyComposable
        get() {
            val scheme = MaterialTheme.colorScheme
            val extras = FitTrackExtras.colors
            return if (scheme.surface == extras.inputBackground) extras.borderLight else scheme.outline
        }
}

