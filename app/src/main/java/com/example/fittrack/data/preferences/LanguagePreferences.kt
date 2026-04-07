package com.example.fittrack.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object LanguagePreferencesKeys {
    val LANGUAGE_TAG: Preferences.Key<String> = stringPreferencesKey("language_tag")
}

class LanguagePreferences(private val appContext: Context) {
    val languageTag: Flow<String> =
        appContext.dataStore.data.map { prefs ->
            prefs[LanguagePreferencesKeys.LANGUAGE_TAG] ?: "en"
        }

    suspend fun setLanguageTag(tag: String) {
        appContext.dataStore.edit { prefs ->
            prefs[LanguagePreferencesKeys.LANGUAGE_TAG] = tag
        }
    }
}
