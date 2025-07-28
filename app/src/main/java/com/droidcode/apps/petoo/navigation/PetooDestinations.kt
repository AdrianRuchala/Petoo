package com.droidcode.apps.petoo.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

interface PetooDestination {
    val route: String
}

object Settings: PetooDestination {
    override val route = "settings"
}

object Login: PetooDestination {
    override val route = "login"
}

object PetCard: PetooDestination {
    override val route = "petCard"
}

object AddPet: PetooDestination {
    override val route = "addPet"
}

object Visits: PetooDestination {
    override val route = "visits"
}

object AddVisit: PetooDestination {
    override val route = "addVisit"
}

object VisitDetails: PetooDestination {
    override val route = "visitDetails?visitId={visitId}"
}

sealed class PetooNavItems(val route: String, var icon: ImageVector, val title: String) {
    object PetCard : PetooNavItems("petCard", Icons.Default.Menu, "Lista")
    object Settings : PetooNavItems("settings", Icons.Default.Settings, "Ustawienia")
    object Visits: PetooNavItems("visits", Icons.Default.DateRange, "Wizyty")
}
