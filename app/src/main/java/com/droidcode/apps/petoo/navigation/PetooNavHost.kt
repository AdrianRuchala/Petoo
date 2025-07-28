package com.droidcode.apps.petoo.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.droidcode.apps.petoo.MainScreenViewModel
import com.droidcode.apps.petoo.VisitDetailsScreen
import com.droidcode.apps.petoo.VisitsScreen
import com.droidcode.apps.petoo.settings.Profile
import com.droidcode.apps.petoo.settings.ProfileScreen
import com.droidcode.apps.petoo.settings.SettingsInfo
import com.droidcode.apps.petoo.userViews.AddPetScreen
import com.droidcode.apps.petoo.userViews.AddVisitScreen
import com.droidcode.apps.petoo.userViews.PetCardScreen
import com.droidcode.apps.petoo.vetViews.VetAddVisitScreen
import com.droidcode.apps.petoo.vetViews.VetPetCardScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun PetooNavHost(
    modifier: Modifier,
    navController: NavHostController,
    role: String,
    onLogout: () -> Unit
) {
    val viewModel: MainScreenViewModel = viewModel()
    var isUserVet by remember { mutableStateOf(false) }
    val userData = Profile(
        name = Firebase.auth.currentUser?.displayName,
        email = Firebase.auth.currentUser?.email,
        profilePictureUrl = Firebase.auth.currentUser?.photoUrl.toString()
    )

    viewModel.isUserVet()
    viewModel.checkUserRole(role, viewModel) { checkedIsUserVet ->
        isUserVet = checkedIsUserVet
    }

    if (role == "Weterynarz") {
        viewModel.addVetToDatabase(
            Firebase.auth.currentUser?.displayName,
            Firebase.auth.currentUser?.email
        )
    }

    NavHost(
        navController = navController,
        startDestination = PetCard.route,
        modifier = modifier.padding()
    ) {
        composable(Settings.route) {
            ProfileScreen(
                modifier,
                settingsInfo = SettingsInfo(
                    Profile(
                        userData.name,
                        userData.email,
                        userData.profilePictureUrl
                    ), "1.0"
                ),
                onLogout
            )
        }

        composable(PetCard.route) {
            if (isUserVet) {
                VetPetCardScreen(Modifier, viewModel)
            } else {
                PetCardScreen(
                    modifier = Modifier,
                    viewModel,
                ) { navController.navigateSingleTopTo(AddPet.route) }
            }

        }

        composable(AddPet.route) {
            AddPetScreen(Modifier, viewModel) { navController.navigateUp() }
        }

        composable(Visits.route) {
            VisitsScreen(
                Modifier,
                viewModel,
                role,
                { navController.navigateSingleTopTo(AddVisit.route) },
                { visitId -> navController.navigateSingleTopTo("visitDetails?visitId=$visitId") }
            )

        }

        composable(AddVisit.route) {
            if (isUserVet) {
                VetAddVisitScreen(
                    Modifier,
                    viewModel
                ) { navController.navigateSingleTopTo(Visits.route) }
            } else {
                AddVisitScreen(
                    Modifier,
                    viewModel
                ) { navController.navigateSingleTopTo(Visits.route) }
            }
        }

        composable(VisitDetails.route) { backStackEntry ->
            val visitId = backStackEntry.arguments?.getString("visitId") ?: ""
            viewModel.readVisitData(isUserVet)
            val petData = viewModel.visitViewState.value.find { it.visitId == visitId }
            petData?.let{
                VisitDetailsScreen(Modifier, visitId, viewModel) { navController.navigateSingleTopTo(Visits.route) }
            }
        }
    }
}

fun NavController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
