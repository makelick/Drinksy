package com.makelick.drinksy.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.makelick.drinksy.cocktails.view.HomeCocktailsScreen
import com.makelick.drinksy.cocktails.view.CocktailScreen
import com.makelick.drinksy.lists.view.CreateListScreen
import com.makelick.drinksy.lists.view.ListScreen
import com.makelick.drinksy.reviews.view.CreateReviewScreen
import com.makelick.drinksy.reviews.view.ReviewsScreen
import com.makelick.drinksy.lists.view.ListsScreen
import com.makelick.drinksy.lists.view.ShareListScreen
import com.makelick.drinksy.login.view.LoginScreen
import com.makelick.drinksy.profile.view.ProfileScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                navigateToCocktail = {
                    navController.navigate(Screen.HomeCocktails.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.HomeCocktails.route) {
            HomeCocktailsScreen(
                navigateToCocktail = { elementId ->
                    navController.navigate(Screen.Cocktail.createRoute(elementId))
                },
                navigateToCreateCocktail = {
                    // TODO("Not yet implemented")
                },
            )
        }

        composable(
            route = Screen.Cocktail.route,
            arguments = listOf(
                navArgument("elementId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val elementId = backStackEntry.arguments?.getString("elementId") ?: ""
            CocktailScreen(
                cocktailId = elementId,
                onBackPressed = { navController.popBackStack() },
                navigateToReviews = {
                    navController.navigate(Screen.Reviews.createRoute(elementId))
                },
                navigateToLists = {
                    navController.navigate(Screen.Lists.route)
                }
            )
        }

        composable(
            route = Screen.Reviews.route,
            arguments = listOf(
                navArgument("elementId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cocktailId = backStackEntry.arguments?.getString("elementId") ?: ""
            ReviewsScreen(
                cocktailId = cocktailId,
                onBackPressed = { navController.popBackStack() },
                navigateToAddReview = {
                    navController.navigate(Screen.CreateReview.createRoute(cocktailId))
                }
            )
        }

        composable(
            route = Screen.CreateReview.route,
            arguments = listOf(
                navArgument("elementId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cocktailId = backStackEntry.arguments?.getString("elementId") ?: ""
            CreateReviewScreen(
                cocktailId = cocktailId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Lists.route) {
            ListsScreen(
                navigateToListDetails = { listId ->
                    navController.navigate(Screen.List.createRoute(listId))
                },
                navigateToCreateList = {
                    navController.navigate(Screen.CreateList.createRoute(""))
                }
            )
        }

        composable(
            route = Screen.List.route,
            arguments = listOf(
                navArgument("listId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId") ?: ""
            ListScreen(
                listId = listId,
                navigateBack = { navController.popBackStack() },
                navigateToShareList = {
                    navController.navigate(Screen.ShareList.createRoute(listId))
                },
                navigateToCocktail = { cocktailId ->
                    navController.navigate(Screen.Cocktail.createRoute(cocktailId))
                }
            )
        }

        composable(
            route = Screen.CreateList.route,
            arguments = listOf(
                navArgument("listId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId") ?: ""
            CreateListScreen(
                listId = listId,
                navigateBack = { navController.popBackStack() },
                navigateToShareList = {
                    navController.navigate(Screen.ShareList.createRoute(listId))
                }
            )
        }

        composable(
            route = Screen.ShareList.route,
            arguments = listOf(
                navArgument("listId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId") ?: ""
            ShareListScreen(
                listId = listId,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                navigateToFavorites = {
                    // TODO("Not yet implemented")
                },
                navigateToMyLists = {
                    // TODO("Not yet implemented")
                },
                navigateToMyReviews = {
                    // TODO("Not yet implemented")
                }
            )
        }
    }
}