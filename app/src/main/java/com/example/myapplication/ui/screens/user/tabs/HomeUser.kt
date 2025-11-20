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
import com.example.myapplication.model.Place
import com.example.myapplication.ui.components.Button
import com.example.myapplication.ui.components.CompactSearchBar
import com.example.myapplication.ui.components.slipCard
import com.example.myapplication.viewmodel.PlacesViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotationGroup
import com.mapbox.maps.plugin.annotation.generated.pointAnnotationOptions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeUser(navController: NavHostController, placesViewModel: PlacesViewModel) {
    var query by rememberSaveable { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val places by placesViewModel.places.collectAsState()
    val filtered = remember(places, query) {
        places.filter {
            query.isBlank() || it.title.contains(query, ignoreCase = true)
        }
    }
    val annotations = remember(filtered) {
        filtered.map { place ->
            pointAnnotationOptions {
                geometry(Point.fromLngLat(place.location.longitude, place.location.latitude))
                iconImage("marker-15")
                textField(place.id)
            }
        }
    }
    var selectedPlace by remember { mutableStateOf<com.example.myapplication.model.Place?>(null) }

    val viewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(12.0)
            center(Point.fromLngLat(-75.69321, 4.5140139))
            pitch(0.0)
            bearing(0.0)
        }
    }

    LaunchedEffect(selectedPlace?.id) {
        selectedPlace?.let { place ->
            viewportState.setCameraOptions {
                center(Point.fromLngLat(place.location.longitude, place.location.latitude))
                zoom(14.0)
            }
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
        if (selectedPlace != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch {
                        sheetState.hide()
                        selectedPlace = null
                    }
                },
                sheetState = sheetState
            ) {
                selectedPlace?.let { place ->
                    PlaceBottomSheet(place = place)
                }
            }
        }
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(innerPadding)
        ) {
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
                                .clip(RoundedCornerShape(222.dp)),
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
                    MapboxMap(
                        Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        mapViewportState = viewportState,
                    ) {
                        PointAnnotationGroup(
                            annotations = annotations,
                            onClick = { annotation ->
                                val clicked = filtered.firstOrNull { it.id == annotation.textField }
                                if (clicked != null) {
                                    scope.launch {
                                        selectedPlace = clicked
                                        sheetState.show()
                                    }
                                }
                                true
                            }
                        )
                    }
                    Surface(
                        tonalElevation = 1.dp,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(bottom = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Text(stringResource(R.string.Lugares_cerca))
                        }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 8.dp)
                        ) {
                            items(filtered, key = { it.id }) { place ->
                                slipCard(
                                    name = place.title,
                                    description = place.description,
                                    imageUrl = place.images.firstOrNull() ?: "",
                                    onClick = {
                                        scope.launch {
                                            selectedPlace = place
                                            sheetState.show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceBottomSheet(place: Place) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(place.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        AsyncImage(
            model = place.images.firstOrNull(),
            contentDescription = place.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Text(place.description, style = MaterialTheme.typography.bodyMedium)
        Text("${place.address} • ${place.city.displayName}")
        androidx.compose.material3.Button(
            onClick = { /* navegar a detalles */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.green),
                contentColor = Color.White
            )
        ) {
            Text("Ver detalles")
        }
    }
}
