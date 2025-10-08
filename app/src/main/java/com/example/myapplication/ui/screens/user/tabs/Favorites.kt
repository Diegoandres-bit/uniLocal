package com.example.myapplication.ui.screens.user.tabs

import TopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import co.edu.eam.lugaresapp.ui.user.bottombar.BottomBarUser
import com.example.myapplication.R
import com.example.myapplication.config.Navigation
import com.example.myapplication.ui.components.Button
import com.example.myapplication.ui.components.CompactSearchBar
import com.example.myapplication.ui.components.FavoriteTopBar
import com.example.myapplication.ui.screens.user.navs.UserRouteTab

@Composable
fun Favoritos(navController: NavHostController){
    var query by rememberSaveable { mutableStateOf("") }

    Scaffold(

        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
        ,
        topBar = {
            FavoriteTopBar(
                navController,
                text = "Favoritos",
                icon1 = Icons.Outlined.ArrowBackIosNew,
                icon2 = Icons.Outlined.Settings
            )
        },
        bottomBar = {
            Row (
                Modifier.padding(10.dp)
            ){
                Button(
                    onClick = { },
                    modifier = Modifier
                        .width(160.dp)
                        .clip(RoundedCornerShape(222.dp)),
                    text = stringResource(R.string.Gestionar_Listas),
                    color = colorResource(R.color.lightgreen),
                    contentColor = colorResource(R.color.black)
                )
                Button(
                    onClick = { navController.navigate(UserRouteTab.HomeUser::class.qualifiedName!!) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                    },
                    modifier = Modifier
                        .width(120.dp)
                        .clip(RoundedCornerShape(222.dp)),
                    text = stringResource(R.string.Abrir_Mapa),
                    color = colorResource(R.color.green),
                    contentColor = colorResource(R.color.white)
                )
            }



        },
    ) { innerPadding ->
        Column(modifier = Modifier
            .background(Color.White)
            .padding(innerPadding)



        ) {
            Divider(
                modifier = Modifier.padding(vertical = 2.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )

            CompactSearchBar(
                query = query,
                onQueryChange = { query = it },
                "Buscar en Favoritos"
            )
            Divider(
                modifier = Modifier.padding(vertical = 2.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    Button(
                        onClick = {query="" },

                        modifier = Modifier
                            .width(70.dp)
                            .clip(RoundedCornerShape(22.dp)),
                        text = "All",
                        color = colorResource(R.color.lightgreen),
                        contentColor = colorResource(R.color.teal_700)
                    )
                }
                item {
                    Button(
                        onClick = {query="Restaurant" },

                        modifier = Modifier
                            .width(120.dp)
                            .clip(RoundedCornerShape(22.dp)),
                        text = "Restaurant",
                        color = colorResource(R.color.lightgreen),
                        contentColor = colorResource(R.color.teal_700)
                    )
                }
                item {
                    Button(
                        onClick = {query="Bar" },

                        modifier = Modifier
                            .width(70.dp)
                            .clip(RoundedCornerShape(22.dp)),
                        text = "Bar",
                        color = colorResource(R.color.lightgreen),
                        contentColor = colorResource(R.color.teal_700)
                    )
                }
            }
            Divider(
                modifier = Modifier.padding(vertical = 2.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorResource(R.color.lightgreen)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Sin favoritos",
                        tint = Color.Gray,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = "Aún no tienes favoritos",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))


                Text(
                    text = "Toca el ícono de corazón en un lugar para guardarlo aquí.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {  },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.green)
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .width(200.dp)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Explorar lugares",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }


        }
    }
}
