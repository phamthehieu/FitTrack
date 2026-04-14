package com.example.fittrack.ui.locale

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import java.util.Locale

/**
 * Gắn locale cho cây Compose mà không recreate Activity (khác với [androidx.appcompat.app.AppCompatDelegate.setApplicationLocales]).
 * [languageTag] dạng ISO như "en", "vi".
 */
@Composable
fun ProvideAppLocale(
    languageTag: String,
    content: @Composable () -> Unit,
) {
    val baseContext = LocalContext.current
    val configuration = remember(languageTag) {
        Configuration(baseContext.resources.configuration).apply {
            setLocale(Locale.forLanguageTag(languageTag))
        }
    }
    val localizedContext = remember(languageTag) {
        baseContext.createConfigurationContext(configuration)
    }
    CompositionLocalProvider(
        LocalConfiguration provides configuration,
        LocalContext provides localizedContext,
        LocalResources provides localizedContext.resources,
    ) {
        content()
    }
}
