package com.makelick.drinksy.lists.data

import com.makelick.drinksy.cocktails.data.Cocktail

data class CocktailList(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val type: ListType = ListType.COMPILATION,
    val cocktails: List<Cocktail> = emptyList(),
    val creatorUser: String = "",
    val isPublic: Boolean = false,
)

enum class ListType {
    MENU,
    COMPILATION,
}

data class CocktailWithPrice(
    val cocktail: Cocktail,
    val price: String = "",
    val comment: String = ""
)
