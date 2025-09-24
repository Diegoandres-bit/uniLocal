package com.example.myapplication.config

import ProfileScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.screens.CreateAccount
import com.example.myapplication.screens.CreatePlaceScreen
import com.example.myapplication.screens.LoginScreen
import com.example.myapplication.screens.ResetPasswordScreenForm
import com.example.myapplication.viewmodel.CreatePlaceIntents
import com.example.myapplication.viewmodel.CreatePlaceViewModel
import com.example.myapplication.viewmodel.ProfileViewModel


@Composable
fun Navigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RouteScreen.Login
    ){

        composable<RouteScreen.Login> {
            LoginScreen(
                onLogin = {navController.navigate(RouteScreen.CreatePlace)},
                onRegister = {navController.navigate(RouteScreen.Register)},
                onRecoverPassword = {navController.navigate(RouteScreen.RecoverPassword)}
            )
        }

        composable<RouteScreen.Register> {
            CreateAccount(
                onBack = {navController.navigate(RouteScreen.Login)}
            )
        }

        composable<RouteScreen.RecoverPassword> {
            ResetPasswordScreenForm(
                onBack = {navController.navigate(RouteScreen.Login)}
            )
        }

        composable<RouteScreen.Profile> {
            // 1) Obtén el ViewModel en Compose
            val vm: ProfileViewModel = viewModel()

            // 2) Observa el estado de forma lifecycle-aware
            val ui = vm.uiState.collectAsStateWithLifecycle().value

            // 3) Pasa el estado y callbacks a tu pantalla
            ProfileScreen(
                uiState = ui,
                onBack = { navController.navigate(RouteScreen.Login) },
                onToggleEdit = { vm.toggleEdit() },
                onSave = { context -> vm.save(context) },
                onNameChange = vm::updateName,
                onUsernameChange = vm::updateUsername,
                onCityChange = vm::updateCity
            )

        }

        composable<RouteScreen.Places> {
//            PlacesScreen()
        }

        composable<RouteScreen.CreatePlace> {
            val vm: CreatePlaceViewModel = viewModel()
            val ui = vm.ui.collectAsState().value

            CreatePlaceScreen(
                ui = ui,
                intents = CreatePlaceIntents(
                    onBack = { navController.navigate(RouteScreen.Places) },
                    onDeleteDraft = { vm.deleteDraft() },
                    onStepClick = { /* si quieres permitir saltar de paso */ },

                    onNameChange = vm::onNameChange,
                    onDescriptionChange = vm::onDescriptionChange,
                    onCategoryChange = vm::onCategoryChange,
                    onPhonesChange = vm::onPhonesChange,

                    onAddPhotoClick = { /* abre picker y llama vm.addPhoto(uri) */ },
                    onRemovePhoto = vm::removePhoto,

                    onSaveDraft = vm::saveDraft,
                    onNext = vm::next,
                    onPrevious = { vm.back() }
                )
            )
        }

    }

}

