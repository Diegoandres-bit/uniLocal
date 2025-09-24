package com.example.myapplication.config


import kotlinx.serialization.Serializable

sealed class RouteScreen {

    @Serializable
    data object Profile : RouteScreen()

    @Serializable
    data object CreatePlace : RouteScreen()

    @Serializable
    data object Places : RouteScreen()

    @Serializable
    data object Login : RouteScreen()

    @Serializable
    data object Register : RouteScreen()

    @Serializable
    data object RecoverPassword : RouteScreen()


}