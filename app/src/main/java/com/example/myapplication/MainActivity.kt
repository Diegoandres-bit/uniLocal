package com.example.myapplication

import com.example.myapplication.ui.components.BottomSheetExample
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.screens.user.navs.ContentUser
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
                MyApplicationTheme {
                   
                    //HomeModerator()
                    }
            }
        }
    }
