package com.makelick.drinksy.lists.data

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.makelick.drinksy.cocktails.data.Cocktail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseCocktailRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : CocktailRepo {

    companion object {
        private const val COCKTAILS_COLLECTION = "cocktails"
        private const val IMAGES_STORAGE_PATH = "cocktail_images"
    }

    override suspend fun getAllCocktails(): List<Cocktail> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection(COCKTAILS_COLLECTION)
                    .get()
                    .await()

                snapshot.documents.mapNotNull { document ->
                    document.toCocktail()
                }
            } catch (e: Exception) {
                throw CocktailRepositoryException("Failed to fetch cocktails: ${e.message}", e) as Throwable
            }
        }
    }

    override suspend fun getCocktailById(id: String): Cocktail? {
        return withContext(Dispatchers.IO) {
            try {
                val document = firestore.collection(COCKTAILS_COLLECTION)
                    .document(id)
                    .get()
                    .await()

                document.toCocktail()
            } catch (e: Exception) {
                throw CocktailRepositoryException("Failed to fetch cocktail with id $id: ${e.message}", e)
            }
        }
    }

    override suspend fun uploadImage(uri: Uri): String {
        return withContext(Dispatchers.IO) {
            try {
                val imageId = UUID.randomUUID().toString()
                val imageRef = storage.reference
                    .child("$IMAGES_STORAGE_PATH/$imageId.jpg")

                imageRef.putFile(uri).await()
                val downloadUrl = imageRef.downloadUrl.await()

                downloadUrl.toString()
            } catch (e: Exception) {
                throw CocktailRepositoryException("Failed to upload image: ${e.message}", e)
            }
        }
    }

    // Additional methods for completeness
    suspend fun searchCocktails(query: String): List<Cocktail> {
        return withContext(Dispatchers.IO) {
            try {
                // Note: Firestore doesn't support full-text search natively
                // For production, consider using Algolia or implementing client-side filtering
                val snapshot = firestore.collection(COCKTAILS_COLLECTION)
                    .orderBy("name")
                    .startAt(query)
                    .endAt(query + "\uf8ff")
                    .get()
                    .await()

                snapshot.documents.mapNotNull { document ->
                    document.toCocktail()
                }
            } catch (e: Exception) {
                throw CocktailRepositoryException("Failed to search cocktails: ${e.message}", e)
            }
        }
    }

    suspend fun getCocktailsByCategory(category: String): List<Cocktail> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection(COCKTAILS_COLLECTION)
                    .whereEqualTo("category", category)
                    .get()
                    .await()

                snapshot.documents.mapNotNull { document ->
                    document.toCocktail()
                }
            } catch (e: Exception) {
                throw CocktailRepositoryException("Failed to fetch cocktails by category: ${e.message}", e)
            }
        }
    }

    suspend fun saveCocktail(cocktail: Cocktail): String {
        return withContext(Dispatchers.IO) {
            try {
                val cocktailMap = cocktail.toFirestoreMap()

                if (cocktail.id.isEmpty()) {
                    // Create new cocktail
                    val docRef = firestore.collection(COCKTAILS_COLLECTION).add(cocktailMap).await()
                    docRef.id
                } else {
                    // Update existing cocktail
                    firestore.collection(COCKTAILS_COLLECTION)
                        .document(cocktail.id)
                        .set(cocktailMap, SetOptions.merge())
                        .await()
                    cocktail.id
                }
            } catch (e: Exception) {
                throw CocktailRepositoryException("Failed to save cocktail: ${e.message}", e)
            }
        }
    }

    suspend fun deleteCocktail(id: String) {
        return withContext(Dispatchers.IO) {
            try {
                firestore.collection(COCKTAILS_COLLECTION)
                    .document(id)
                    .delete()
                    .await()
            } catch (e: Exception) {
                throw CocktailRepositoryException("Failed to delete cocktail: ${e.message}", e)
            }
        }
    }
}
