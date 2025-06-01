package com.makelick.drinksy.lists.data

interface CocktailListRepo {
    suspend fun saveCocktailList(cocktailList: CocktailList)
    suspend fun getCocktailListById(id: String): CocktailList?
    suspend fun updateCocktailList(cocktailList: CocktailList)
    suspend fun getPublicCocktailLists(): List<CocktailList>
}