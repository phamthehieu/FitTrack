package com.example.fittrack.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fittrack.R
import com.example.fittrack.ui.theme.FitTrackTheme
import com.example.fittrack.ui.theme.ThemeMode

@Composable
fun HomeScreen(
    currentThemeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeChange: (ThemeMode) -> Unit = {},
    onLogout: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = stringResource(id = R.string.home_title))
            Text(text = stringResource(id = R.string.home_theme_label, currentThemeMode.name))
            Button(onClick = { onThemeModeChange(ThemeMode.SYSTEM) }) {
                Text(stringResource(id = R.string.home_theme_system))
            }
            Button(onClick = { onThemeModeChange(ThemeMode.LIGHT) }) {
                Text(stringResource(id = R.string.home_theme_light))
            }
            Button(onClick = { onThemeModeChange(ThemeMode.DARK) }) {
                Text(stringResource(id = R.string.home_theme_dark))
            }
            Button(onClick = onLogout) {
                Text(stringResource(id = R.string.home_logout))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    FitTrackTheme {
        HomeScreen()
    }
}
