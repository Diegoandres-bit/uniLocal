package com.example.myapplication.ui.screens.moderator.tabs

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.myapplication.viewmodel.UsersViewModel


@Composable
fun Profile(usersViewModel: UsersViewModel){

    val users by usersViewModel.users.collectAsState()

    LazyColumn{
       items( users){
          Text(text = it.name)
       }

    }
}