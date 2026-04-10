package com.example.fittrack.data.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureAuthStorage(
    private val context: Context,
) {
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val prefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveCredentials(email: String, password: String) {
        prefs.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, password)
            .apply()
    }

    fun getCredentials(): Pair<String, String>? {
        val email = prefs.getString(KEY_EMAIL, null)?.trim().orEmpty()
        val password = prefs.getString(KEY_PASSWORD, null).orEmpty()
        if (email.isBlank() || password.isBlank()) return null
        return email to password
    }

    fun clearCredentials() {
        prefs.edit()
            .remove(KEY_EMAIL)
            .remove(KEY_PASSWORD)
            .apply()
    }

    private companion object {
        private const val PREFS_NAME = "secure_auth_prefs"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
    }
}

