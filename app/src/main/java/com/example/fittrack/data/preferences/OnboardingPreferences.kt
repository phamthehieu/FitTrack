package com.example.fittrack.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object OnboardingPreferencesKeys {
    val COMPLETED: Preferences.Key<Boolean> = booleanPreferencesKey("onboarding_completed")
}

class OnboardingPreferences(private val appContext: Context) {
    val completed: Flow<Boolean> =
        appContext.dataStore.data.map { prefs ->
            prefs[OnboardingPreferencesKeys.COMPLETED] ?: false
        }

    suspend fun setCompleted(value: Boolean) {
        appContext.dataStore.edit { prefs ->
            prefs[OnboardingPreferencesKeys.COMPLETED] = value
        }
    }
}

