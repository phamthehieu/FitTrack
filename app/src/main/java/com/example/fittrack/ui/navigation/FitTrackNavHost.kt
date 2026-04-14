package com.example.fittrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fittrack.data.security.SecureAuthStorage
import com.example.fittrack.ui.screens.auth.login.LoginScreen
import com.example.fittrack.ui.screens.auth.register.RegisterScreen
import com.example.fittrack.ui.screens.home.HomeScreen
import com.example.fittrack.ui.screens.splash.SplashScreen
import com.example.fittrack.ui.theme.ThemeMode
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FitTrackNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    currentThemeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeChange: (ThemeMode) -> Unit = {},
    currentLanguageTag: String = "en",
    onLanguageTagChange: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val secureAuthStorage = SecureAuthStorage(context)

    NavHost(
        navController = navController,
        startDestination = FitTrackRoutes.SPLASH,
        modifier = modifier,
    ) {
        composable(FitTrackRoutes.SPLASH) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(FitTrackRoutes.HOME) {
                        popUpTo(FitTrackRoutes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(FitTrackRoutes.loginRoute()) {
                        popUpTo(FitTrackRoutes.SPLASH) { inclusive = true }
                    }
                },
            )
        }
        composable(
            route = FitTrackRoutes.LOGIN_ROUTE,
            arguments = listOf(
                navArgument("email") { defaultValue = "" },
            ),
        ) { backStackEntry ->
            val prefillEmail = backStackEntry.arguments?.getString("email").orEmpty()
            LoginScreen(
                currentThemeMode = currentThemeMode,
                currentLanguageTag = currentLanguageTag,
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
                onLoginSuccess = {
                    navController.navigate(FitTrackRoutes.HOME) {
                        popUpTo(FitTrackRoutes.LOGIN_ROUTE) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(FitTrackRoutes.REGISTER) },
                prefillEmail = prefillEmail,
            )
        }
        composable(FitTrackRoutes.REGISTER) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onRegistered = { email ->
                    navController.navigate(FitTrackRoutes.loginRoute(email)) {
                        popUpTo(FitTrackRoutes.REGISTER) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(FitTrackRoutes.HOME) {
            HomeScreen(
                currentThemeMode = currentThemeMode,
                onThemeModeChange = onThemeModeChange,
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    secureAuthStorage.clearCredentials()
                    navController.navigate(FitTrackRoutes.loginRoute()) {
                        popUpTo(FitTrackRoutes.HOME) { inclusive = true }
                    }
                },
            )
        }
    }
}
