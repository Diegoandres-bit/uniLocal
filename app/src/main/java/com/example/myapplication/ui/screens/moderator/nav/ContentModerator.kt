package com.example.myapplication.ui.screens.moderator.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.moderator.tabs.Dashboard
import com.example.myapplication.ui.screens.moderator.tabs.History
import com.example.myapplication.ui.screens.moderator.tabs.Profile


@Composable
fun ContentModerator(padding: PaddingValues , navController: NavHostController) {


    NavHost(
        modifier = Modifier.padding(padding),
        navController = navController,
        startDestination = RouteTab.Dashboard

    ){


        composable<RouteTab.Dashboard> {
            Dashboard()
        }

        composable<RouteTab.History> {
            History()
        }


        composable<RouteTab.Profile> {
            Profile()
        }




    }

}