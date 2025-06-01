package com.makelick.drinksy.cocktails.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.jvm.java

class FirestoreRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getAllCocktails(): List<Cocktail> {
        return try {
            val snapshot = firestore.collection("cocktails").get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Cocktail::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getCocktailById(id: String): Cocktail? {
        return try {
            val doc = firestore.collection("cocktails").document(id).get().await()
            doc.toObject(Cocktail::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun toggleFavoriteStatus(cocktailId: String, userId: String) {
        try {
            val userDoc = firestore.collection("users").document(userId).get().await()
            val user = userDoc.toObject(com.makelick.drinksy.profile.data.User::class.java)
            if (user != null) {

                val updatedFavorites = if (user.favoriteCocktails.contains(cocktailId)) {
                    user.favoriteCocktails - cocktailId
                } else {
                    user.favoriteCocktails + cocktailId
                }
                firestore.collection("users").document(userId).update("favoriteCocktails", updatedFavorites).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}