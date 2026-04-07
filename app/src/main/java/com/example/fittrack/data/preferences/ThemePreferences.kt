package com.example.fittrack.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.fittrack.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object ThemePreferencesKeys {
    val THEME_MODE: Preferences.Key<String> = stringPreferencesKey("theme_mode")
}

class ThemePreferences(private val appContext: Context) {
    val themeMode: Flow<ThemeMode> =
        appContext.dataStore.data.map { prefs ->
            val raw = prefs[ThemePreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            runCatching { ThemeMode.valueOf(raw) }.getOrDefault(ThemeMode.SYSTEM)
        }

    suspend fun setThemeMode(mode: ThemeMode) {
        appContext.dataStore.edit { prefs ->
            prefs[ThemePreferencesKeys.THEME_MODE] = mode.name
        }
    }
}
