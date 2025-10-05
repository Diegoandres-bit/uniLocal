package co.edu.eam.lugaresapp.ui.user.bottombar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapplication.R
import com.example.myapplication.ui.screens.user.navs.UserRouteTab

@Composable
fun BottomBarUser(
    navController: NavHostController,
    showTopBar: (Boolean) -> Unit,
    showFAB: (Boolean) -> Unit,
    titleTopBar: (Int) -> Unit
){

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    LaunchedEffect(currentDestination) {
        val destination = Destination.entries.find { it.route::class.qualifiedName == currentDestination?.route }
        if (destination != null) {
            showTopBar(destination.showTopBar)
            showFAB(destination.showFAB)
            titleTopBar(destination.label)
        }
    }

    NavigationBar(
        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth(),
    ){

        Destination.entries.forEachIndexed { index, destination ->

            val isSelected = currentDestination?.route == destination.route::class.qualifiedName

            NavigationBarItem(
                label = {
                    Text(
                        text = stringResource(destination.label)
                    )
                },
                onClick = {
                    navController.navigate(destination.route){
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
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
    val route: UserRouteTab,
    val label: Int,
    val icon: ImageVector,
    val showTopBar: Boolean = true,
    val showFAB: Boolean = false
){
    HOME(UserRouteTab.HomeUser, R.string.Home, Icons.Default.Home ),
    SEARCH(UserRouteTab.Favoritos, R.string.Favoritos, Icons.Default.Search, showTopBar = false),
}