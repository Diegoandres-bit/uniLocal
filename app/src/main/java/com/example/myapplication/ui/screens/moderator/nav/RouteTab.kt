package com.example.myapplication.ui.screens.moderator.nav


import kotlinx.serialization.Serializable

sealed class RouteTab {

    @Serializable
    data object Dashboard : RouteTab()

    @Serializable
    data object History : RouteTab()

    @Serializable
    data object Profile : RouteTab()



}
