package com.example.myapplication.ui.screens.user.tabs

import TopBar
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import co.edu.eam.lugaresapp.ui.user.bottombar.BottomBarUser
import com.example.myapplication.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeUser(navController: NavHostController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(
                imageId = R.drawable.usimbol,
                text = "UniLocal",
                icon1 = Icons.Outlined.FavoriteBorder,
                icon2 = Icons.Outlined.Person
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
                modifier = Modifier.padding(vertical = 1.dp),
                color = Color.Gray,
                thickness = 1.dp
            )

        }
    }
}
