package com.example.fittrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fittrack.data.preferences.OnboardingPreferences
import com.example.fittrack.data.security.SecureAuthStorage
import com.example.fittrack.ui.screens.auth.login.LoginScreen
import com.example.fittrack.ui.screens.auth.register.RegisterScreen
import com.example.fittrack.ui.screens.home.HomeScreen
import com.example.fittrack.ui.screens.main.MainScreen
import com.example.fittrack.ui.screens.onboarding.OnBoardingScreen
import com.example.fittrack.ui.screens.splash.SplashScreen
import com.example.fittrack.ui.theme.ThemeMode
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()
    val secureAuthStorage = SecureAuthStorage(context)
    val onboardingPreferences = OnboardingPreferences(context)

    NavHost(
        navController = navController,
        startDestination = FitTrackRoutes.SPLASH,
        modifier = modifier,
    ) {
        composable(FitTrackRoutes.SPLASH) {
            SplashScreen(
                onNavigateToHome = {
                    scope.launch {
                        val completed = onboardingPreferences.completed.first()
                        navController.navigate(if (completed) FitTrackRoutes.MAIN else FitTrackRoutes.ONBOARDING) {
                            popUpTo(FitTrackRoutes.SPLASH) { inclusive = true }
                        }
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
                currentLanguageTag = currentLanguageTag,
                onToggleLanguage = {
                    val next = if (currentLanguageTag.lowercase().startsWith("vi")) "en" else "vi"
                    onLanguageTagChange(next)
                },
                onLoginSuccess = {
                    scope.launch {
                        val completed = onboardingPreferences.completed.first()
                        navController.navigate(if (completed) FitTrackRoutes.MAIN else FitTrackRoutes.ONBOARDING) {
                            popUpTo(FitTrackRoutes.LOGIN_ROUTE) { inclusive = true }
                        }
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
        composable(FitTrackRoutes.ONBOARDING) {
            OnBoardingScreen(
                onFinished = {
                    scope.launch {
                        onboardingPreferences.setCompleted(true)
                        navController.navigate(FitTrackRoutes.MAIN) {
                            popUpTo(FitTrackRoutes.ONBOARDING) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onLogoutToLogin = {
                    FirebaseAuth.getInstance().signOut()
                    secureAuthStorage.clearCredentials()
                    scope.launch { onboardingPreferences.setCompleted(false) }
                    navController.navigate(FitTrackRoutes.loginRoute()) {
                        popUpTo(FitTrackRoutes.ONBOARDING) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(FitTrackRoutes.MAIN) {
            MainScreen()
        }
        composable(FitTrackRoutes.HOME) {
            HomeScreen(
            )
        }
    }
}
