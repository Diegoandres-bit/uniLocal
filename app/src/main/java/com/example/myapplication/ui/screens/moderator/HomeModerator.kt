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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.model.User
import com.example.myapplication.ui.screens.moderator.bottomBar.BottomBarModerator
import com.example.myapplication.ui.screens.moderator.nav.ContentModerator
import com.example.myapplication.ui.screens.moderator.nav.RouteTab


@Composable
fun HomeModerator(
    user: User?
) {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBarModerator(navController = navController)},
        bottomBar = { BottomBarModerator(
            navController= navController
        ) }
    ) { padding ->
        ContentModerator( navController= navController,padding = padding, currentUser = user)
    }
}







@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarModerator(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination: NavDestination? = backStackEntry?.destination

    // Funciona con navegación tipada sin depender de hasRoute<>
    val route = destination?.route ?: ""
    val title = when (route) {
        RouteTab.Dashboard::class.qualifiedName -> "Dashboard Moderador"
        RouteTab.History::class.qualifiedName   -> "Historial Lugares"
        RouteTab.Profile::class.qualifiedName   -> "Perfil Moderador"
        else                                    -> "Centro de Moderación"
    }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
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
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Configuración",
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    )
}





