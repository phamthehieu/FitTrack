package com.example.fittrack.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val DATASTORE_NAME = "fittrack_preferences"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)
