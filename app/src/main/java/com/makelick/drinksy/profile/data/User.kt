package com.makelick.drinksy.profile.data

import com.makelick.drinksy.cocktails.data.Cocktail

data class User(
    val id: String,
    val name: String,
    val profilePictureUrl: String,
    val favoriteCocktails: List<Cocktail>,
    val tastes: List<String>,
)
