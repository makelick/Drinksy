package com.makelick.drinksy.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

@Composable
fun BottomNavigationBar(
    navController: NavController,
    items: List<Screen>,
    currentRoute: String?
) {
    NavigationBar {
        items.forEach { screen ->
            val selected = currentRoute == screen.route

            NavigationBarItem(
                icon = {
                    when (screen) {
                        Screen.HomeCocktails -> Icon(
                            Icons.Default.LocalBar,
                            contentDescription = "Cocktails"
                        )

                        Screen.Lists -> Icon(
                            Icons.AutoMirrored.Filled.List,
                            contentDescription = "Lists"
                        )
                        Screen.Profile -> Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile"
                        )

                        else -> {}
                    }
                },
                label = {
                    when (screen) {
                        Screen.HomeCocktails -> Text("Cocktails")
                        Screen.Lists -> Text("Lists")
                        Screen.Profile -> Text("Profile")
                        else -> {}
                    }
                },
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
