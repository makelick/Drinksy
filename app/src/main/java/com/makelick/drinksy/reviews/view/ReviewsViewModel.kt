package com.makelick.drinksy.reviews.view

import androidx.lifecycle.ViewModel
import com.makelick.drinksy.profile.data.User
import com.makelick.drinksy.reviews.data.Review
import com.makelick.drinksy.reviews.data.ReviewWithUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.sql.Date
import javax.inject.Inject

@HiltViewModel
class ReviewsViewModel @Inject constructor() : ViewModel() {
    private val _reviews = MutableStateFlow<List<ReviewWithUser>>(emptyList())
    val reviews: StateFlow<List<ReviewWithUser>> = _reviews

    private val _cocktailName = MutableStateFlow("")
    val cocktailName: StateFlow<String> = _cocktailName

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadReviews(cocktailId: String) {
        // This would typically fetch data from a repository
        // For this example, we're just simulating data
        _isLoading.value = true

        // Simulate network delay
        // In a real app, this would be an API or database call
        // fetchReviewsForCocktail(cocktailId).collect { result ->
        //     _reviews.value = result
        //     _isLoading.value = false
        // }

        // For demo purposes, load the cocktail name
        _cocktailName.value = "Mojito" // Would be loaded from repository
        _isLoading.value = false

        _reviews.value = listOf(
            ReviewWithUser(
                review = Review(
                    id = "1",
                    userId = "1",
                    rating = 4f,
                    comment = "Great cocktail!",
                    date = Date(System.currentTimeMillis())
                ),
                user = User(
                    id = "1",
                    name = "John Doe",
                    profilePictureUrl = ""
                )
            ),
            ReviewWithUser(
                review = Review(
                    id = "2",
                    userId = "2",
                    rating = 5f,
                    comment = "Absolutely loved it!",
                    date = Date(System.currentTimeMillis())
                ),
                user = User(
                    id = "2",
                    name = "Jane Smith",
                    profilePictureUrl = ""
                )
            )
        )
    }
}