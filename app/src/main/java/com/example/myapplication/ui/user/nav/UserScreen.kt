package com.example.myapplication.ui.user.nav

import kotlinx.serialization.Serializable

sealed class UserScreen {

    @Serializable
    data object Comments: UserScreen()

    @Serializable
    data object CreatePlace: UserScreen()

    @Serializable
    data object EditProfile: UserScreen()

    @Serializable
    data object Favorite: UserScreen()

}