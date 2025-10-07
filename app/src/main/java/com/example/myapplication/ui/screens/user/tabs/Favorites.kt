package com.example.myapplication.ui.screens.user.tabs

import TopBar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import co.edu.eam.lugaresapp.ui.user.bottombar.BottomBarUser
import com.example.myapplication.config.Navigation
import com.example.myapplication.ui.components.CompactSearchBar
import com.example.myapplication.ui.components.FavoriteTopBar

@Composable
fun Favoritos(navController: NavHostController){
    var query by rememberSaveable { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            FavoriteTopBar(
                navController,
                text = "Favoritos",
                icon1 = Icons.Outlined.ArrowBackIosNew,
                icon2 = Icons.Outlined.Settings
            )
        },
        bottomBar = {
            BottomBarUser(
                navController = navController,
                showTopBar = { },
                showFAB = { },
                titleTopBar = { }
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Divider(
                modifier = Modifier.padding(vertical = 2.dp),
                color = Color.Gray,
                thickness = 1.dp
            )

            CompactSearchBar(
                query = query,
                onQueryChange = { query = it },
                "Buscar en Favoritos"
            )
            Divider(
                modifier = Modifier.padding(vertical = 2.dp),
                color = Color.Gray,
                thickness = 1.dp
            )

        }
    }
}
