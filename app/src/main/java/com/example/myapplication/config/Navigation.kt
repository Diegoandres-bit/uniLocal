package com.example.myapplication.config

import EditProfileScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.model.Role
import com.example.myapplication.model.User
import com.example.myapplication.ui.screens.CreateAccount
import com.example.myapplication.ui.screens.CreatePlaceScreen
import com.example.myapplication.ui.screens.LoginScreen
import com.example.myapplication.ui.screens.MyPlacesScreen
import com.example.myapplication.ui.screens.PlaceDetailsScreen
import com.example.myapplication.ui.screens.PlacesScreen
import com.example.myapplication.ui.screens.ProfileScreen
import com.example.myapplication.ui.screens.QuickReviewScreen
import com.example.myapplication.ui.screens.ResetPasswordScreenForm
import com.example.myapplication.ui.screens.moderator.HomeModerator
import com.example.myapplication.ui.screens.user.tabs.HomeUser
import com.example.myapplication.viewmodel.CreatePlaceIntents
import com.example.myapplication.viewmodel.CreatePlaceViewModel
import com.example.myapplication.viewmodel.PlacesViewModel
import com.example.myapplication.viewmodel.ProfileViewModel
import com.example.myapplication.viewmodel.UsersViewModel

@Composable
fun Navigation() {

    val navController = rememberNavController()
    val placesViewModel: PlacesViewModel = viewModel()
    val usersViewModel: UsersViewModel = viewModel()
    val loggedUser by usersViewModel.loggedInUser.collectAsState()

    LaunchedEffect(loggedUser?.id) {
        placesViewModel.setLoggedIn(loggedUser != null)
    }

    NavHost(
        navController = navController,
        startDestination = RouteScreen.Login
    ) {

        composable<RouteScreen.Login> {
            LoginScreen(
                onLogin = { user: User ->
                        if (user.role == Role.ADMIN) {
                            navController.currentBackStackEntry?.savedStateHandle?.set("user", user)
                            navController.navigate(RouteScreen.Home)
                        } else {
                            navController.navigate(RouteScreen.EditProfile)
                        }
                },
                onRegister = { navController.navigate(RouteScreen.Register) },
                onRecoverPassword = { navController.navigate(RouteScreen.RecoverPassword) },
                usersViewModel = usersViewModel
            )
        }

        composable<RouteScreen.Register> {
            CreateAccount(
                onBack = { navController.navigate(RouteScreen.Login) }
            )
        }

        composable<RouteScreen.RecoverPassword> {
            ResetPasswordScreenForm(
                onBack = { navController.navigate(RouteScreen.Login) },
                usersViewModel = usersViewModel
            )
        }

        composable<RouteScreen.EditProfile> {
            // 1) Obtén el ViewModel en Compose
            val vm: ProfileViewModel = viewModel()

            // 2) Observa el estado de forma lifecycle-aware
            val ui = vm.uiState.collectAsStateWithLifecycle().value

            // 3) Pasa el estado y callbacks a tu pantalla
            EditProfileScreen(
                uiState = ui,
                onBack = { navController.navigate(RouteScreen.Login) },
                onToggleEdit = { vm.toggleEdit() },
                onSave = { context -> vm.save(context) },
                onNameChange = vm::updateName,
                onUsernameChange = vm::updateUsername,
                onCityChange = vm::updateCity
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
                onToggleEdit = { navController.navigate(RouteScreen.EditProfile) },
                onSave = { context -> vm.save(context) },
                onNameChange = vm::updateName,
                onUsernameChange = vm::updateUsername,
                onCityChange = vm::updateCity
            )

        }

        composable<RouteScreen.MyPlaces> {
            MyPlacesScreen(
                placesViewModel,
                onBack = {},
                onCreate = {navController.navigate(RouteScreen.CreatePlace)},
                onComments = { place ->
                    placesViewModel.selectPlace(place.id)
                    navController.navigate(RouteScreen.Review)
                }
            )
        }

        composable<RouteScreen.Places> {
            PlacesScreen(
                placesViewModel,
                onOpenPlace = {},
                onDirections = {p -> "s"},
                onCall = {},
                onOpenFilters = {}
            )
        }

        composable<RouteScreen.PlaceDetails> {
            PlaceDetailsScreen(
                placesViewModel,
                onBack = {},
                onCall = {},
                onShare = {},
                onDirections = {},
                onCommentRate = {},
                placeId = "1",
                onToggleFavorite = {}
            )
        }

        composable<RouteScreen.Review> {
            QuickReviewScreen(
                placesViewModel,
                onBack = {navController.navigate(RouteScreen.MyPlaces)},
                onLogin = { navController.navigate(RouteScreen.Login) }
            )
        }

        composable<RouteScreen.CreatePlace> {
            val vm: CreatePlaceViewModel = viewModel()
            val ui = vm.ui.collectAsState().value

            CreatePlaceScreen(
                ui = ui,
                intents = CreatePlaceIntents(
                    onBack = { navController.navigate(RouteScreen.MyPlaces) },
                    onDeleteDraft = { vm.deleteDraft() },
                    onStepClick = { /* si quieres permitir saltar de paso */ },

                    onNameChange = vm::onNameChange,
                    onDescriptionChange = vm::onDescriptionChange,
                    onCategoryChange = vm::onCategoryChange,
                    onPhonesChange = vm::onPhonesChange,

                    onAddPhotoClick = vm::simulatePhotoUpload,
                    onRemovePhoto = vm::removePhoto,

                    onSaveDraft = vm::saveDraft,
                    onNext = vm::next,
                    onPrevious = { vm.back() }
                )
            )
        }

        composable<RouteScreen.Moderator> {
            val user = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<User>("user")

            // Si quieres, valida null por seguridad
            if (user == null) {
                // Manejo de error / fallback
                navController.popBackStack()
                return@composable
            }

            HomeModerator(user = user)
        }

        composable<RouteScreen.Home> {
            HomeUser(
                navController,
                placesViewModel,
            )
        }


    }

}
