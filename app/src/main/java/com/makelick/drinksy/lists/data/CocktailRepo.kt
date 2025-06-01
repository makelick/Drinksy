package com.makelick.drinksy.lists.data

import android.net.Uri
import com.makelick.drinksy.cocktails.data.Cocktail

interface CocktailRepo {
    suspend fun getAllCocktails(): List<Cocktail>
    suspend fun getCocktailById(id: String): Cocktail?
    suspend fun uploadImage(uri: Uri): String
}