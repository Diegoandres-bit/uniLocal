package com.example.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.example.myapplication.ui.screens.user.navs.UserRouteTab

@Composable
fun FavoriteTopBar(
    navController: NavHostController,
    text: String,
    icon1: ImageVector,
    icon2: ImageVector,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            IconButton(
                onClick = { navController.navigate(UserRouteTab.HomeUser::class.qualifiedName!!) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
                          },
                modifier = Modifier
            ) {
                Icon(
                    imageVector = icon1,
                    contentDescription = "Icon 1",
                )

            }
            Spacer(modifier = Modifier.width(25.dp))
            Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(
                Alignment.CenterVertically))
        }

        Row {
            IconButton(
                onClick = {

                },
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .background(colorResource(R.color.lightgreen))
                    .size(36.dp)

            ) {
                Icon(
                    imageVector = icon2,
                    contentDescription = "Icon 1",
                )
            }

        }
    }
}
