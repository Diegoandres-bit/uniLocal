package com.example.myapplication.ui.screens.user.tabs

import TopBar
import android.R.attr.query
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import com.example.myapplication.ui.components.Button
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person

import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import co.edu.eam.lugaresapp.ui.user.bottombar.BottomBarUser
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.ui.components.CompactSearchBar
import com.example.myapplication.viewmodel.PlacesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ViewModelConstructorInComposable")
@Composable
fun HomeUser(navController: NavHostController) {
val placesViewModel = PlacesViewModel()
    var query by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(
                imageId = R.drawable.usimbol,
                text = stringResource(R.string.uniLocal),
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
        Column(modifier = Modifier
            .background(Color.White)
            .padding(innerPadding)) {

            Divider(
                modifier = Modifier.padding(vertical = 1.dp),
                color = Color.Gray,
                thickness = 1.dp
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .background(Color.White)
                ) {
                CompactSearchBar(
                    query = query,
                    onQueryChange = { query = it }
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .width(132.dp)
                                .clip(RoundedCornerShape(222.dp)),
                            text = "Restaurante",
                            color = colorResource(R.color.lightgreen),
                            contentColor = colorResource(R.color.teal_700)
                        )
                    }

                    item {
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .width(78.dp)
                                .clip(RoundedCornerShape(222.dp)),
                            text = "Café",
                            color = colorResource(R.color.lightgreen),
                            contentColor = colorResource(R.color.teal_700)
                        )
                    }

                    item {
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .width(90.dp)
                                .clip(RoundedCornerShape(222.dp))

                            ,
                            text = "1-20 km",
                            color = colorResource(R.color.lightgreen),
                            contentColor = colorResource(R.color.teal_700)
                        )
                    }

                    item {
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .width(120.dp)
                                .clip(RoundedCornerShape(222.dp)),
                            text = "Abierto",
                            color = colorResource(R.color.lightgreen),
                            contentColor = colorResource(R.color.teal_700)
                        )
                    }
                }
                Box {
                    AsyncImage(
                        model ="https://motor.elpais.com/wp-content/uploads/2022/01/google-maps-22.jpg",
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentScale = ContentScale.FillBounds
                    )
                }


            }

    }
}
}

