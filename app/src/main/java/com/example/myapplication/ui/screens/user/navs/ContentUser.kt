package com.example.myapplication.ui.screens.user.navs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.user.tabs.Favoritos
import com.example.myapplication.ui.screens.user.tabs.HomeUser

@Composable
fun ContentUser(padding: PaddingValues, navController: NavHostController) {
    NavHost(
        modifier = Modifier.padding(padding),
        navController = navController,
        startDestination = UserRouteTab.HomeUser
    ) {
        composable<UserRouteTab.HomeUser> {
            HomeUser(navController = navController)
        }
        composable<UserRouteTab.Favoritos> {
            Favoritos(navController = navController)
        }
    }
}
