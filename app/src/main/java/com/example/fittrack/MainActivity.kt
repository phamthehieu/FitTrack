package com.example.fittrack

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.fittrack.data.preferences.LanguagePreferences
import com.example.fittrack.data.preferences.ThemePreferences
import com.example.fittrack.ui.navigation.FitTrackNavHost
import com.example.fittrack.ui.theme.FitTrackTheme
import com.example.fittrack.ui.theme.ThemeMode
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themePreferences = remember { ThemePreferences(applicationContext) }
            val languagePreferences = remember { LanguagePreferences(applicationContext) }
            val scope = rememberCoroutineScope()
            val themeMode by themePreferences.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            val languageTag by languagePreferences.languageTag.collectAsState(initial = "en")

            FitTrackTheme(themeMode = themeMode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FitTrackNavHost(
                        modifier = Modifier.padding(innerPadding),
                        currentThemeMode = themeMode,
                        onThemeModeChange = { selectedMode ->
                            val desiredNightMode = when (selectedMode) {
                                ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                                ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                                ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                            }
                            if (AppCompatDelegate.getDefaultNightMode() != desiredNightMode) {
                                AppCompatDelegate.setDefaultNightMode(desiredNightMode)
                            }
                            scope.launch {
                                themePreferences.setThemeMode(selectedMode)
                            }
                        },
                        currentLanguageTag = languageTag,
                        onLanguageTagChange = { selectedTag ->
                            val desired = LocaleListCompat.forLanguageTags(selectedTag)
                            val current = AppCompatDelegate.getApplicationLocales()
                            if (current.toLanguageTags() != desired.toLanguageTags()) {
                                AppCompatDelegate.setApplicationLocales(desired)
                            }
                            scope.launch {
                                languagePreferences.setLanguageTag(selectedTag)
                            }
                        },
                    )
                }
            }
        }
    }
}
