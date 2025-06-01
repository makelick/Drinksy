package com.makelick.drinksy.core

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.makelick.drinksy.cocktails.view.CocktailScreen
import com.makelick.drinksy.cocktails.view.HomeCocktailsScreen
import com.makelick.drinksy.lists.view.CocktailListEditorScreen
import com.makelick.drinksy.lists.view.ListScreen
import com.makelick.drinksy.lists.view.ListsScreen
import com.makelick.drinksy.login.view.LoginScreen
import com.makelick.drinksy.profile.view.FavoritesScreen
import com.makelick.drinksy.profile.view.ProfileScreen
import com.makelick.drinksy.reviews.view.CreateReviewScreen
import com.makelick.drinksy.reviews.view.ReviewsScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    deepLinkViewModel: DeepLinkViewModel
) {

    val pendingDeepLinkState = deepLinkViewModel.pendingDeepLink.collectAsState()
    val pendingDeepLink = pendingDeepLinkState.value
    val context = LocalContext.current

    LaunchedEffect(pendingDeepLink) {
        pendingDeepLink?.let { deepLink ->
            when (deepLink.type) {
                DeepLinkType.COCKTAIL -> {
                    navController.navigate(Screen.HomeCocktails.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                    navController.navigate(Screen.Cocktail.createRoute(deepLink.id))
                }

                DeepLinkType.LIST -> {
                    navController.navigate(Screen.HomeCocktails.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                    navController.navigate(Screen.List.createRoute(deepLink.id))
                }

                DeepLinkType.UNKNOWN -> {}
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    if (pendingDeepLink != null) {
                        deepLinkViewModel.clearPendingDeepLink()
                    } else {
                        navController.navigate(Screen.HomeCocktails.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
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
                },
                onShareCocktail = {
                    val shareLink = DeepLinkHandler.generateCocktailLink(elementId)
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Check out this cocktail: $shareLink")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Cocktail"))
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
                navigateToCocktail = { cocktailId ->
                    navController.navigate(Screen.Cocktail.createRoute(cocktailId))
                },
                onShareList = {
                    val shareLink = DeepLinkHandler.generateListLink(listId)
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Check out cocktails: $shareLink")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Cocktail List"))
                }
            )
        }

        composable(
            route = Screen.CreateList.route,
            arguments = listOf(
                navArgument("listId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId")
            CocktailListEditorScreen(
                listId = listId,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                navigateToFavorites = {
                    navController.navigate(Screen.Favorites.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                },
                navigateToMyLists = {
                    // TODO("Not yet implemented")
                },
                navigateToMyReviews = {
                    // TODO("Not yet implemented")
                },
                navigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        // clear the back stack
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            Screen.Favorites.route
        ) {
            FavoritesScreen(
                navigateBack = { navController.popBackStack() },
                navigateToCocktail = { cocktailId ->
                    navController.navigate(Screen.Cocktail.createRoute(cocktailId))
                }
            )
        }
    }
}