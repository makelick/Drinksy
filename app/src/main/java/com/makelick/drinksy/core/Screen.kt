package com.makelick.drinksy.core

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object HomeCocktails : Screen("cocktail")
    data object Cocktail : Screen("element/{elementId}") {
        fun createRoute(elementId: String) = "element/$elementId"
    }
    data object Reviews : Screen("reviews/{elementId}") {
        fun createRoute(elementId: String) = "reviews/$elementId"
    }
    data object CreateReview : Screen("create_review/{elementId}") {
        fun createRoute(elementId: String?) = "create_review/$elementId"
    }
    data object Lists : Screen("lists")
    data object List : Screen("lists/{listId}") {
        fun createRoute(listId: String) = "lists/$listId"
    }
    data object CreateList : Screen("create_list/{listId}") {
        fun createRoute(listId: String?) = "create_list/$listId"
    }
    data object ShareList : Screen("share_list/{listId}") {
        fun createRoute(listId: String) = "share_list/$listId"
    }
    data object Profile : Screen("profile")
}