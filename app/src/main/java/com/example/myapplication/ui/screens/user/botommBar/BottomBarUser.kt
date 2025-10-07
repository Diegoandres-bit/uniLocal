package co.edu.eam.lugaresapp.ui.user.bottombar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    LaunchedEffect(currentDestination) {
        val destination =
            Destination.entries.find { it.route::class.qualifiedName == currentDestination?.route }
        if (destination != null) {
            showTopBar(destination.showTopBar)
            showFAB(destination.showFAB)
            titleTopBar(destination.label)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Destination.entries.forEach { destination ->
            val isSelected = currentDestination?.route == destination.route::class.qualifiedName

            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) Color(0xFFDFF3EA) else Color.Transparent,
                label = ""
            )
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) Color(0xFF006A4E) else Color(0xFF808080),
                label = ""
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(backgroundColor, RoundedCornerShape(12.dp))
                    .clickable {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = destination.icon,
                    contentDescription = stringResource(destination.label),
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = stringResource(destination.label),
                    fontSize = 12.sp,
                    color = contentColor
                )
            }
        }
    }
}

enum class Destination(
    val route: UserRouteTab,
    val label: Int,
    val icon: ImageVector,
    val showTopBar: Boolean = true,
    val showFAB: Boolean = false
) {
    HOME(UserRouteTab.HomeUser, R.string.Home, Icons.Default.Map),
    SEARCH(UserRouteTab.Favoritos, R.string.Favoritos, Icons.Default.FavoriteBorder, showTopBar = false),
}
