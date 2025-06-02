package com.makelick.drinksy.login.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.makelick.drinksy.profile.data.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    fun getCurrentUser() = auth.currentUser

    suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("User is null")
            val user = getUserFromFirestore(firebaseUser.uid)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, password: String, name: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("User creation failed")

            val user = User(
                id = firebaseUser.uid,
                name = name,
                profilePictureUrl = firebaseUser.photoUrl?.toString() ?: "",
                favoriteCocktails = emptyList(),
                tastes = emptyList()
            )

            createUserDocument(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("Google sign-in failed")

            // Check if user document exists, if not create it
            val existingUser = getUserFromFirestore(firebaseUser.uid)
            if (existingUser.id.isEmpty()) {
                val newUser = User(
                    id = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    profilePictureUrl = firebaseUser.photoUrl?.toString() ?: "",
                    favoriteCocktails = emptyList(),
                    tastes = emptyList()
                )
                createUserDocument(newUser)
                Result.success(newUser)
            } else {
                Result.success(existingUser)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserFromFirestore(userId: String): User {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                document.toObject(User::class.java) ?: User()
            } else {
                User()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            User()
        }
    }

    private suspend fun createUserDocument(user: User) {
        firestore.collection("users").document(user.id).set(user).await()
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun updateUsername(userId: String, newName: String) {
        firestore.collection("users").document(userId)
            .update("name", newName)
            .await()
    }

    suspend fun uploadProfilePicture(userId: String, imageUri: Uri) : String {
        val storageRef = FirebaseStorage.getInstance().reference
        val profilePicRef = storageRef.child("profile_pictures/$userId.jpg")

        kotlin.runCatching {
            profilePicRef.delete().await()
        }

        // Upload the file to Firebase Storage
        profilePicRef.putFile(imageUri).await()

        // Get the download URL
        val downloadUrl = profilePicRef.downloadUrl.await().toString()

        // Update Firestore with the new profile picture URL
        firestore.collection("users").document(userId)
            .update("profilePictureUrl", downloadUrl)
            .await()

        return downloadUrl
    }

    suspend fun updatePictureUrl(userId: String, newUrl: String) {
        firestore.collection("users").document(userId)
            .update("profilePictureUrl", newUrl)
            .await()
    }

    suspend fun updateTastes(userId: String, tastes: List<String>) {
        firestore.collection("users").document(userId)
            .update("tastes", tastes)
            .await()
    }
}
