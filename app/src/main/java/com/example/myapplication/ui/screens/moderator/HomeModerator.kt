package com.example.myapplication.ui.screens.moderator

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.model.User
import com.example.myapplication.ui.screens.moderator.bottomBar.BottomBarModerator
import com.example.myapplication.ui.screens.moderator.nav.ContentModerator


@Composable
fun HomeModerator(
    user: User?
) {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBarModerator() },
        bottomBar = { BottomBarModerator(
            navController= navController
        ) }
    ) { padding ->
        ContentModerator( navController= navController,padding = padding)
    }
}







@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarModerator() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Dashboard de Moderación",
                style = MaterialTheme.typography.titleLarge
            )
        },

        navigationIcon = {
            Icon(
                imageVector = Icons.Outlined.Dashboard,
                contentDescription = "Menú",
                modifier = Modifier.padding(start = 8.dp)
            )
        },

        actions = {
            Icon(
                imageVector = Icons.Outlined.Settings, // ⚙️
                contentDescription = "Configuración",
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    )
}







