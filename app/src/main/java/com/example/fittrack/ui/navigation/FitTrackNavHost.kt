package com.example.fittrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fittrack.ui.screens.auth.login.LoginScreen
import com.example.fittrack.ui.screens.auth.register.RegisterScreen
import com.example.fittrack.ui.screens.home.HomeScreen
import com.example.fittrack.ui.theme.ThemeMode

@Composable
fun FitTrackNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    currentThemeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeChange: (ThemeMode) -> Unit = {},
    currentLanguageTag: String = "en",
    onLanguageTagChange: (String) -> Unit = {},
) {
    NavHost(
        navController = navController,
        startDestination = FitTrackRoutes.LOGIN,
        modifier = modifier,
    ) {
        composable(FitTrackRoutes.LOGIN) {
            LoginScreen(
                currentThemeMode = currentThemeMode,
                onToggleTheme = {
                    val next = when (currentThemeMode) {
                        ThemeMode.SYSTEM -> ThemeMode.DARK
                        ThemeMode.LIGHT -> ThemeMode.DARK
                        ThemeMode.DARK -> ThemeMode.LIGHT
                    }
                    onThemeModeChange(next)
                },
                onToggleLanguage = {
                    val next = if (currentLanguageTag.lowercase().startsWith("vi")) "en" else "vi"
                    onLanguageTagChange(next)
                },
            )
        }
        composable(FitTrackRoutes.REGISTER) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(FitTrackRoutes.HOME) {
            HomeScreen(
                currentThemeMode = currentThemeMode,
                onThemeModeChange = onThemeModeChange,
                onLogout = {
                    navController.navigate(FitTrackRoutes.LOGIN) {
                        popUpTo(FitTrackRoutes.HOME) { inclusive = true }
                    }
                },
            )
        }
    }
}
