package com.makelick.drinksy.lists.data

import com.makelick.drinksy.cocktails.data.Cocktail

data class CocktailList(
    val id: String,
    val name: String,
    val imageUrl: String,
    val description: String,
    val type: ListType,
    val cocktails: List<Cocktail>,
    val creatorUserId: String,
    val isPublic: Boolean = false,
)

enum class ListType {
    MENU,
    COMPILATION,
}