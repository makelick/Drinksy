package com.makelick.drinksy.lists.data

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseCocktailListRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : CocktailListRepo {

    companion object {
        private const val LISTS_COLLECTION = "lists"
        private const val LIST_IMAGES_STORAGE_PATH = "list_images"
    }

    override suspend fun saveCocktailList(cocktailList: CocktailList) {
        try {
            Log.d("FirebaseCocktailListRepo", "Saving) cocktail list: ${cocktailList.name}")
            val listMap = cocktailList.toFirestoreMap()

            val docRef = firestore.collection(LISTS_COLLECTION)
                .add(listMap)
                .await()

            Log.e("FirebaseCocktailListRepo", "Saved cocktail list with ID: ${docRef.id}")
            docRef.id
        } catch (e: Exception) {
            throw CocktailListRepositoryException("Failed to save cocktail list: ${e.message}", e)
        }
    }

    override suspend fun getCocktailListById(id: String): CocktailList? {
        return withContext(Dispatchers.IO) {
            try {
                val document = firestore.collection(LISTS_COLLECTION)
                    .document(id)
                    .get()
                    .await()

                document.toCocktailList(::getUserNameById)
            } catch (e: Exception) {
                throw CocktailListRepositoryException("Failed to fetch cocktail list with id $id: ${e.message}", e)
            }
        }
    }

    override suspend fun updateCocktailList(cocktailList: CocktailList) {
        return withContext(Dispatchers.IO) {
            try {
                val listMap = cocktailList.toFirestoreMap()

                firestore.collection(LISTS_COLLECTION)
                    .document(cocktailList.id)
                    .set(listMap, SetOptions.merge())
                    .await()
            } catch (e: Exception) {
                throw CocktailListRepositoryException("Failed to update cocktail list: ${e.message}", e)
            }
        }
    }

    suspend fun getUserNameById(userId: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val document = firestore.collection("users")
                    .document(userId)
                    .get()
                    .await()

                document.getString("name")
            } catch (e: Exception) {
                throw CocktailListRepositoryException("Failed to fetch user name: ${e.message}", e)
            }
        }
    }

    // Additional methods for completeness
    suspend fun getCocktailListsByUser(userId: String): List<CocktailList> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection(LISTS_COLLECTION)
                    .whereEqualTo("creatorUserId", userId)
                    .get()
                    .await()

                snapshot.documents.mapNotNull { document ->
                    document.toCocktailList(::getUserNameById)
                }
            } catch (e: Exception) {
                throw CocktailListRepositoryException("Failed to fetch user's cocktail lists: ${e.message}", e)
            }
        }
    }

    override suspend fun getPublicCocktailLists(): List<CocktailList> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection(LISTS_COLLECTION)
                    .whereEqualTo("isPublic", true)
                    .get()
                    .await()

                snapshot.documents.mapNotNull { document ->
                    document.toCocktailList(::getUserNameById)
                }
            } catch (e: Exception) {
                throw CocktailListRepositoryException("Failed to fetch public cocktail lists: ${e.message}", e)
            }
        }
    }

    suspend fun getCocktailListsByType(type: ListType): List<CocktailList> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection(LISTS_COLLECTION)
                    .whereEqualTo("type", type.name)
                    .get()
                    .await()

                snapshot.documents.mapNotNull { document ->
                    document.toCocktailList(::getUserNameById)
                }
            } catch (e: Exception) {
                throw CocktailListRepositoryException("Failed to fetch cocktail lists by type: ${e.message}", e)
            }
        }
    }

    suspend fun deleteCocktailList(id: String) {
        return withContext(Dispatchers.IO) {
            try {
                firestore.collection(LISTS_COLLECTION)
                    .document(id)
                    .delete()
                    .await()
            } catch (e: Exception) {
                throw CocktailListRepositoryException("Failed to delete cocktail list: ${e.message}", e)
            }
        }
    }

    suspend fun uploadListImage(uri: Uri): String {
        return withContext(Dispatchers.IO) {
            try {
                val imageId = UUID.randomUUID().toString()
                val imageRef = storage.reference
                    .child("$LIST_IMAGES_STORAGE_PATH/$imageId.jpg")

                val uploadTask = imageRef.putFile(uri).await()
                val downloadUrl = imageRef.downloadUrl.await()

                downloadUrl.toString()
            } catch (e: Exception) {
                throw CocktailListRepositoryException("Failed to upload list image: ${e.message}", e)
            }
        }
    }

    suspend fun searchCocktailLists(query: String): List<CocktailList> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection(LISTS_COLLECTION)
                    .orderBy("name")
                    .startAt(query)
                    .endAt(query + "\uf8ff")
                    .whereEqualTo("isPublic", true)
                    .get()
                    .await()

                snapshot.documents.mapNotNull { document ->
                    document.toCocktailList(::getUserNameById)
                }
            } catch (e: Exception) {
                throw CocktailListRepositoryException("Failed to search cocktail lists: ${e.message}", e)
            }
        }
    }
}