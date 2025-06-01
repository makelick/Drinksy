package com.makelick.drinksy.lists.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.makelick.drinksy.cocktails.data.Cocktail
import com.makelick.drinksy.reviews.data.Review
import java.sql.Date

internal fun DocumentSnapshot.toCocktail(): Cocktail? {
    return try {
        val data = this.data ?: return null

        Cocktail(
            id = this.id,
            name = data["name"] as? String ?: "",
            description = data["description"] as? String ?: "",
            imageUrl = data["imageUrl"] as? String ?: "",
            ingredients = (data["ingredients"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            instructions = data["instructions"] as? String ?: "",
            category = data["category"] as? String ?: "",
            rating = (data["rating"] as? Number)?.toFloat() ?: 0f,
            reviews = (data["reviews"] as? List<*>)?.mapNotNull { reviewData ->
                (reviewData as? Map<*, *>)?.let { reviewMap ->
                    Review(
                        id = reviewMap["id"] as? String ?: "",
                        userId = reviewMap["userId"] as? String ?: "",
                        rating = (reviewMap["rating"] as? Number)?.toFloat() ?: 0f,
                        comment = reviewMap["comment"] as? String ?: "",
                        date = Date((reviewMap["date"] as? Number)?.toLong() ?: 0L)
                    )
                }
            } ?: emptyList(),
            isFavorite = data["isFavorite"] as? Boolean == true
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

internal suspend fun DocumentSnapshot.toCocktailList(getUserName: suspend (String) -> String?): CocktailList? {
    return try {
        val data = this.data ?: return null

        CocktailList(
            id = this.id,
            name = data["name"] as? String ?: "",
            imageUrl = data["imageUrl"] as? String ?: "",
            description = data["description"] as? String ?: "",
            type = ListType.valueOf(data["type"] as? String ?: ListType.COMPILATION.name),
            cocktails = (data["cocktails"] as? List<*>)?.mapNotNull { cocktailData ->
                (cocktailData as? Map<*, *>)?.let { cocktailMap ->
                    mapToCocktail(cocktailMap)
                }
            } ?: emptyList(),
            creatorUser = getUserName(data["creatorUserId"] as? String ?: "") ?: "Drinksy User",
            isPublic = data["isPublic"] as? Boolean == true
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun mapToCocktail(cocktailMap: Map<*, *>): Cocktail {
    return Cocktail(
        id = cocktailMap["id"] as? String ?: "",
        name = cocktailMap["name"] as? String ?: "",
        description = cocktailMap["description"] as? String ?: "",
        imageUrl = cocktailMap["imageUrl"] as? String ?: "",
        ingredients = (cocktailMap["ingredients"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
        instructions = cocktailMap["instructions"] as? String ?: "",
        category = cocktailMap["category"] as? String ?: "",
        rating = (cocktailMap["rating"] as? Number)?.toFloat() ?: 0f,
        reviews = (cocktailMap["reviews"] as? List<*>)?.mapNotNull { reviewData ->
            (reviewData as? Map<*, *>)?.let { reviewMap ->
                Review(
                    id = reviewMap["id"] as? String ?: "",
                    userId = reviewMap["userId"] as? String ?: "",
                    rating = (reviewMap["rating"] as? Number)?.toFloat() ?: 0f,
                    comment = reviewMap["comment"] as? String ?: "",
                    date = Date((reviewMap["date"] as? Number)?.toLong() ?: 0L)
                )
            }
        } ?: emptyList(),
        isFavorite = cocktailMap["isFavorite"] as? Boolean ?: false
    )
}

internal fun Cocktail.toFirestoreMap(): Map<String, Any> {
    return mapOf(
        "name" to name,
        "description" to description,
        "imageUrl" to imageUrl,
        "ingredients" to ingredients,
        "instructions" to instructions,
        "category" to category,
        "rating" to rating,
        "reviews" to reviews.map { review ->
            mapOf(
                "id" to review.id,
                "userId" to review.userId,
                "rating" to review.rating,
                "comment" to review.comment,
                "date" to review.date
            )
        },
        "isFavorite" to isFavorite,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

internal fun CocktailList.toFirestoreMap(): Map<String, Any> {
    return mapOf(
        "name" to name,
        "imageUrl" to imageUrl,
        "description" to description,
        "type" to type.name,
        "cocktails" to cocktails.map { cocktail ->
            mapOf(
                "id" to cocktail.id,
                "name" to cocktail.name,
                "description" to cocktail.description,
                "imageUrl" to cocktail.imageUrl,
                "ingredients" to cocktail.ingredients,
                "instructions" to cocktail.instructions,
                "category" to cocktail.category,
                "rating" to cocktail.rating,
                "reviews" to cocktail.reviews.map { review ->
                    mapOf(
                        "id" to review.id,
                        "userId" to review.userId,
                        "rating" to review.rating,
                        "comment" to review.comment,
                        "date" to review.date
                    )
                },
                "isFavorite" to cocktail.isFavorite
            )
        },
        "creatorUserId" to creatorUser,
        "isPublic" to isPublic,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

// Custom Exception Classes
class CocktailRepositoryException(message: String, cause: Throwable? = null) : Exception(message, cause)
class CocktailListRepositoryException(message: String, cause: Throwable? = null) : Exception(message, cause)
