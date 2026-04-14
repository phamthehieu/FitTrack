package com.example.fittrack.ui.components.feedback

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

enum class AppSnackKind { Info, Success, Error }

data class AppSnackMessage(
    val text: String,
    val kind: AppSnackKind = AppSnackKind.Info,
)

class AppSnackbarState(
    val hostState: SnackbarHostState,
) {
    suspend fun show(message: AppSnackMessage) {
        hostState.showSnackbar(
            AppSnackVisuals(
                message = message.text,
                kind = message.kind,
                duration = SnackbarDuration.Long,
            )
        )
    }

    suspend fun showInfo(text: String) = show(AppSnackMessage(text, AppSnackKind.Info))
    suspend fun showSuccess(text: String) = show(AppSnackMessage(text, AppSnackKind.Success))
    suspend fun showError(text: String) = show(AppSnackMessage(text, AppSnackKind.Error))
}

@Composable
fun rememberAppSnackbarState(): AppSnackbarState =
    remember { AppSnackbarState(SnackbarHostState()) }

private data class AppSnackVisuals(
    override val message: String,
    val kind: AppSnackKind,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Long,
) : SnackbarVisuals

@Composable
fun AppSnackbarHost(state: AppSnackbarState) {
    SnackbarHost(hostState = state.hostState) { data ->
        val colors = MaterialTheme.colorScheme
        val kind = (data.visuals as? AppSnackVisuals)?.kind ?: AppSnackKind.Info

        Snackbar(
            snackbarData = data,
            containerColor = when (kind) {
                AppSnackKind.Success -> colors.tertiaryContainer
                AppSnackKind.Error -> colors.errorContainer
                AppSnackKind.Info -> colors.inverseSurface
            },
            contentColor = when (kind) {
                AppSnackKind.Success -> colors.onTertiaryContainer
                AppSnackKind.Error -> colors.onErrorContainer
                AppSnackKind.Info -> colors.inverseOnSurface
            },
            actionColor = colors.primary,
        )
    }
}

