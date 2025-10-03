package com.example.myapplication.ui.screens

import AdminCard
import android.R
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun Favorites(){
    Column(){
        AdminCard(
            Modifier,
            "Cafe Andino",
            "cafeteria",
            "bogota",
            "camila",
            "2025-5-7",
            R.drawable.star_on
        )
    }

}
