package com.makelick.drinksy.cocktails.data

import com.makelick.drinksy.reviews.data.Review

data class Cocktail(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val ingredients: List<String> = emptyList(),
    val instructions: String = "",
    val category: String = "",
    val rating: Float = 0f,
    val reviews: List<Review> = emptyList(),
    val isFavorite: Boolean = false,
)
