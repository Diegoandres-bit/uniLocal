package com.example.myapplication.ui.screens.moderator

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.ui.screens.moderator.bottomBar.BottomBarModerator
import com.example.myapplication.ui.screens.moderator.nav.ContentModerator
import com.example.myapplication.ui.screens.moderator.nav.RouteTab
import com.example.myapplication.ui.screens.moderator.tabs.Dashboard
import com.example.myapplication.ui.screens.moderator.tabs.History
import com.example.myapplication.ui.screens.moderator.tabs.Profile


@Composable
fun HomeModerator() {
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
                text = stringResource( R.string.txt_home_title)
            )

        }

    )


}




