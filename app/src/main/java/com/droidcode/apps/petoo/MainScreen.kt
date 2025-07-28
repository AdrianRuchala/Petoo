package com.droidcode.apps.petoo

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.droidcode.apps.petoo.navigation.Login
import com.droidcode.apps.petoo.navigation.PetooNavHost
import com.droidcode.apps.petoo.navigation.PetooNavItems
import com.droidcode.apps.petoo.navigation.navigateSingleTopTo

@Composable
fun MainScreen(
    modifier: Modifier,
    navController: NavHostController,
    role: String,
    onLogout: () -> Unit
) {
    Scaffold(
        bottomBar = { PetooNavigationBar(navController) }
    ) { padding ->
        PetooNavHost(
            modifier = modifier.padding(padding),
            navController = navController,
            role,
            onLogout
        )
    }
}

@Composable
fun PetooNavigationBar(navController: NavController) {
    val items: List<PetooNavItems>

    items = listOf(
        PetooNavItems.PetCard,
        PetooNavItems.Visits,
        PetooNavItems.Settings
    )


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (currentRoute != Login.route) {
        NavigationBar() {
            items.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigateSingleTopTo(item.route)
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(item.title)
                    }
                )
            }
        }
    }
}
