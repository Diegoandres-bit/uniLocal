package com.example.myapplication.ui.screens.user.navs

import kotlinx.serialization.Serializable

sealed class UserRouteTab {

    @Serializable
    data object HomeUser : UserRouteTab()
    @Serializable
    data object Favoritos: UserRouteTab()


}