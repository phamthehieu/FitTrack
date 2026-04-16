package com.example.fittrack.ui.screens.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.mutableIntStateOf
import com.example.fittrack.ui.screens.home.HomeScreen
import com.example.fittrack.ui.theme.FitTrackExtras
import com.example.fittrack.ui.theme.FitTrackTheme

sealed class MainTab(val route: String, val label: String, val icon: ImageVector) {
    data object Home : MainTab("tab/home", "Home", Icons.Default.Home)
    data object Search : MainTab("tab/search", "Search", Icons.Default.Search)
    data object Notifications : MainTab("tab/notifications", "Notifications", Icons.Default.Notifications)
    data object Profile : MainTab("tab/profile", "Profile", Icons.Default.Person)
}

@Composable
fun MainScreen() {
    val tabs = listOf(
        MainTab.Home,
        MainTab.Search,
        MainTab.Notifications,
        MainTab.Profile,
    )

    var selectedRoute by rememberSaveable { mutableStateOf(MainTab.Home.route) }
    val selectedIndex = remember(selectedRoute, tabs) {
        tabs.indexOfFirst { it.route == selectedRoute }.coerceAtLeast(0)
    }
    val haptics = LocalHapticFeedback.current
    var previousIndex by remember { mutableIntStateOf(selectedIndex) }
    val direction = remember(selectedIndex, previousIndex) {
        if (selectedIndex >= previousIndex) 1 else -1
    }
    LaunchedEffect(Unit) {
        snapshotFlow { selectedIndex }.collect { previousIndex = it }
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                selectedRoute = selectedRoute,
                items = tabs,
                onTabSelected = { route ->
                    if (route != selectedRoute) {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        selectedRoute = route
                    }
                },
            )
        },
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            AnimatedContent(
                targetState = selectedRoute,
                label = "main-tabs",
                transitionSpec = {
                    val enter =
                        slideInHorizontally(
                            animationSpec = tween(durationMillis = 260),
                            initialOffsetX = { fullWidth -> fullWidth / 6 * direction },
                        ) + fadeIn(animationSpec = tween(durationMillis = 160))
                    val exit =
                        slideOutHorizontally(
                            animationSpec = tween(durationMillis = 240),
                            targetOffsetX = { fullWidth -> -fullWidth / 8 * direction },
                        ) + fadeOut(animationSpec = tween(durationMillis = 140))
                    enter.togetherWith(exit).using(SizeTransform(clip = false))
                },
            ) { route ->
                when (route) {
                    MainTab.Home.route -> HomeScreen()
                    MainTab.Search.route -> PlaceholderScreen(title = "Search")
                    MainTab.Notifications.route -> PlaceholderScreen(title = "Notifications")
                    MainTab.Profile.route -> PlaceholderScreen(title = "Profile")
                    else -> PlaceholderScreen(title = "Unknown")
                }
            }
        }
    }
}

@Composable
private fun CustomBottomNavigation(
    selectedRoute: String,
    items: List<MainTab>,
    onTabSelected: (String) -> Unit,
) {
    val extras = FitTrackExtras.colors

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 22.dp)
            .height(72.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { tab ->
                val isSelected = selectedRoute == tab.route
                val selectedBg = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f)
                val selectedFg = Color.White
                val unselectedFg = extras.textMuted
                val bg by animateColorAsState(
                    targetValue = if (isSelected) selectedBg else Color.Transparent,
                    animationSpec = tween(durationMillis = 220),
                    label = "tab-bg",
                )
                val fg by animateColorAsState(
                    targetValue = if (isSelected) selectedFg else unselectedFg,
                    animationSpec = tween(durationMillis = 220),
                    label = "tab-fg",
                )
                val iconAlpha by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0.88f,
                    animationSpec = tween(durationMillis = 220),
                    label = "tab-icon-alpha",
                )

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .clickable {
                            onTabSelected(tab.route)
                        },
                    color = bg,
                    contentColor = fg,
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .animateContentSize(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow,
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = fg.copy(alpha = iconAlpha),
                        )
                        AnimatedVisibility(
                            visible = isSelected,
                            enter = fadeIn(tween(140)) + scaleIn(
                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                initialScale = 0.98f,
                            ),
                            exit = fadeOut(tween(110)) + scaleOut(tween(110), targetScale = 0.98f),
                        ) {
                            Text(
                                text = tab.label,
                                color = selectedFg,
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(PaddingValues(20.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MainScreenPreview() {
    FitTrackTheme {
        MainScreen()
    }
}
