package com.example.myapplication.ui.screens.user.tabs

import TopBar
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import co.edu.eam.lugaresapp.ui.user.bottombar.BottomBarUser
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.ui.components.Button
import com.example.myapplication.ui.components.CompactSearchBar
import com.example.myapplication.ui.components.SlipCard
import com.example.myapplication.viewmodel.PlacesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ViewModelConstructorInComposable")
@Composable
fun HomeUser(navController: NavHostController) {
    val placesViewModel = PlacesViewModel()
    var query by rememberSaveable { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val places by placesViewModel.places.collectAsState()
    val filteredPlaces = remember(places, query) {
            when {
                query.equals("Restaurant", ignoreCase = true) -> {
                    places.filter { it.type.name.equals("RESTAURANT", ignoreCase = true) }
                }
                query.equals("Shopping", ignoreCase = true) -> {
                    places.filter { it.type.name.equals("SHOPPING", ignoreCase = true) }
                }
                query.equals("1-20km", ignoreCase = true) -> {
                    places.filter { (it.distanceKm ?: 0.0) in 1.0..20.0 }
                }
                query.isNotBlank() -> {
                    places.filter { it.title.contains(query, ignoreCase = true) }
                }
                else -> places
            }
        }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(
                navController,
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
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(innerPadding)
        ) {
            Divider(
                modifier = Modifier.padding(vertical = 1.dp),
                color = Color.LightGray,
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
                            onClick = {query="Restaurant" },

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
                            onClick = {query="Shopping" },
                            modifier = Modifier
                                .width(130.dp)
                                .clip(RoundedCornerShape(222.dp)),
                            text = "Shopping",
                            color = colorResource(R.color.lightgreen),
                            contentColor = colorResource(R.color.teal_700)
                        )
                    }
                    item {
                        Button(
                            onClick = { query= "1-20km"},
                            modifier = Modifier
                                .width(90.dp)
                                .clip(RoundedCornerShape(222.dp)),
                            text = "1-20 km",
                            color = colorResource(R.color.lightgreen),
                            contentColor = colorResource(R.color.teal_700)
                        )
                    }
                    item {
                        Button(
                            onClick = { query="open"},
                            modifier = Modifier
                                .width(120.dp)
                                .clip(RoundedCornerShape(222.dp)),
                            text = "Open",
                            color = colorResource(R.color.lightgreen),
                            contentColor = colorResource(R.color.teal_700)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    AsyncImage(
                        model = "https://motor.elpais.com/wp-content/uploads/2022/01/google-maps-22.jpg",
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Surface(
                        color=Color.White,
                        tonalElevation = 8.dp,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .background(Color.White)
                        ) {
                            Text(
                                text = stringResource(R.string.Lugares_cerca),
                                fontWeight = FontWeight.SemiBold
                            )

                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.LightGray,
                                thickness = 1.dp
                            )

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredPlaces) { place ->
                                    SlipCard(
                                        name = place.title,
                                        type = place.type,
                                        imageUrl = place.images.firstOrNull() ?: "",
                                        puntuation = place.puntuation,
                                        distance = place.distanceKm ?: 0.0,
                                        onClick = {  }
                                    )
                                }
                            }
                        }
                    }
                }

                    }
            }
        }
    }


