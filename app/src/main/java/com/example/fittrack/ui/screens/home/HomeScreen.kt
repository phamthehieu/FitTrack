package com.example.fittrack.ui.screens.home

import android.content.res.Configuration
import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fittrack.ui.theme.FitTrackExtras
import com.example.fittrack.ui.theme.FitTrackTheme

@Composable
fun HomeScreen(
) {
    val context = LocalContext.current
    val applicationContext = context.applicationContext
    val isPreview =
        LocalInspectionMode.current ||
            LocalView.current.isInEditMode ||
            applicationContext.javaClass.name.startsWith("com.android.layout") ||
            applicationContext.javaClass.name.contains("layout.bridge")

    if (isPreview) {
        HomeScreenContent(fullName = "Nguyễn Văn A")
        return
    }

    val app = applicationContext as? Application
    if (app != null) {
        val vmFactory = remember(app) { HomeViewModel.Factory(app) }
        val vm: HomeViewModel = viewModel(factory = vmFactory)
        val fullName by vm.fullName.collectAsStateWithLifecycle()

        HomeScreenContent(fullName = fullName)
        return
    }

    HomeScreenContent(fullName = "")
}

@Composable
private fun HomeScreenContent(fullName: String) {
    val colorScheme = MaterialTheme.colorScheme
    val extras = FitTrackExtras.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .background(colorScheme.background, CircleShape)
                    .size(48.dp)
                    .border(1.dp, extras.textMuted, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Widgets,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp),
                    tint = extras.textMuted
                )
            }

            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .background(colorScheme.background, CircleShape)
                    .size(48.dp)
                    .border(1.dp, extras.textMuted, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Rounded.NotificationsNone,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp),
                    tint = extras.textMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Hello,", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = extras.textMuted)
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = fullName.ifBlank { "User" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = colorScheme.primary
            )
        }

    }

}

@Preview(showBackground = true, showSystemUi = false)
@Preview(showBackground = true, showSystemUi = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenPreview() {
    FitTrackTheme {
        HomeScreen()
    }
}
