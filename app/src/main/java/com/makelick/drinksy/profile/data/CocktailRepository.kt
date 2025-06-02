package com.makelick.drinksy.profile.data

import com.google.firebase.firestore.FirebaseFirestore

import com.makelick.drinksy.cocktails.data.Cocktail
import com.makelick.drinksy.login.data.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

open class CocktailRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) {
    suspend fun getFavoriteCocktails(userId: String): List<Cocktail> {
        // Get user document from Firestore
        val user = authRepository.getUserFromFirestore(
            userId
        )

        val favoriteIds = user.favoriteCocktails
        if (favoriteIds.isEmpty()) return emptyList()

        // Fetch each cocktail by ID
        val cocktails = mutableListOf<Cocktail>()
        for (id in favoriteIds) {
            val doc = firestore.collection("cocktails").document(id).get().await()
            doc.toObject(Cocktail::class.java)?.copy(id = doc.id)?.let { cocktails.add(it) }
        }
        return cocktails
    }
}
