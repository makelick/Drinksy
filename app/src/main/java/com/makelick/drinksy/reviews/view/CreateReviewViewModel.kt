package com.makelick.drinksy.reviews.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makelick.drinksy.reviews.data.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.sql.Date
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.update

data class CreateReviewUiState(
    val cocktailName: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val isAnonymous: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class CreateReviewViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CreateReviewUiState())
    val uiState: StateFlow<CreateReviewUiState> = _uiState.asStateFlow()

    fun loadCocktail(id: String) {
        // For demo purposes, we'll just set a static name
        _uiState.update { it.copy(cocktailName = "Mojito") }
    }

    fun setRating(rating: Float) {
        _uiState.update { it.copy(rating = rating) }
    }

    fun setComment(comment: String) {
        _uiState.update { it.copy(comment = comment) }
    }

    fun setAnonymous(isAnonymous: Boolean) {
        _uiState.update { it.copy(isAnonymous = isAnonymous) }
    }

    fun submitReview() {
        val currentState = _uiState.value

        // Validate input
        if (currentState.rating == 0f) {
            _uiState.update { it.copy(errorMessage = "Please select a rating") }
            return
        }

        if (currentState.comment.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please add a comment") }
            return
        }

        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                // Create the review
                val userId = if (currentState.isAnonymous) "anonymous" else "current_user_id" // Replace with actual user ID
                val review = Review(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    rating = currentState.rating,
                    comment = currentState.comment,
                    date = Date(System.currentTimeMillis())
                )

                // TODO: Save the review to repository or API
                // reviewRepository.saveReview(review)

                // Update UI state to success
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        isSuccess = true,
                        // Reset fields
                        rating = 0f,
                        comment = "",
                        isAnonymous = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = "Failed to submit review: ${e.message}"
                    )
                }
            }
        }
    }

    fun resetErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetSuccessState() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}