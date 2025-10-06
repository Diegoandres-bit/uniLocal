package com.example.myapplication.ui.screens.moderator.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.model.User
import com.example.myapplication.ui.screens.moderator.tabs.Dashboard
import com.example.myapplication.ui.screens.moderator.tabs.History
import com.example.myapplication.ui.screens.moderator.tabs.Profile
import com.example.myapplication.viewmodel.PlacesViewModel
import com.example.myapplication.viewmodel.UsersViewModel


@Composable
fun ContentModerator(padding: PaddingValues , navController: NavHostController, currentUser: User?) {

    val usersViewModel: UsersViewModel = viewModel()
    val placesViewModel: PlacesViewModel = viewModel()


    NavHost(
        modifier = Modifier.padding(padding),
        navController = navController,
        startDestination = RouteTab.Dashboard

    ) {


        composable<RouteTab.Dashboard> {
            Dashboard(placesViewModel = placesViewModel, usersViewModel = usersViewModel)
        }

        composable<RouteTab.History> {
            History(viewModel = placesViewModel)
        }


        composable<RouteTab.Profile> {
            Profile(usersViewModel = usersViewModel, loggedInUser = currentUser)
        }


    }

}