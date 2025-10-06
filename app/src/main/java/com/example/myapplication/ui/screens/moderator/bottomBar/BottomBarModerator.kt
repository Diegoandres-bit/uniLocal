package com.example.myapplication.ui.screens.moderator.bottomBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapplication.R
import com.example.myapplication.ui.screens.moderator.nav.RouteTab

@Composable
fun BottomBarModerator(navController: NavHostController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination





    NavigationBar() {
        Destination.entries.forEachIndexed { index, destination ->
            val isSelected = currentDestination?.route == destination.route::class.qualifiedName

            NavigationBarItem(
                label = { Text(text = stringResource(destination.label)) },
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = stringResource(destination.label)
                    )
                },
                selected = isSelected
            )
        }

    }

}

enum class Destination(
    val route: RouteTab,
    val label: Int,
    val icon: ImageVector,
){
    DASHBOARD(RouteTab.Dashboard,R.string.txt_dashboard,  Icons.Outlined.Dashboard),
    HISTORY(RouteTab.History,R.string.txt_history,  Icons.Outlined.History),
    PROFILE(RouteTab.Profile,R.string.txt_profile,  Icons.Outlined.Person)
}