package com.makelick.drinksy.profile.data

data class User(
    val id: String = "",
    val name: String = "",
    val profilePictureUrl: String = "",
    val favoriteCocktails: List<String> = emptyList(),
    val tastes: List<String> = emptyList(),
)
