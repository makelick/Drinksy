package com.makelick.drinksy.reviews.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makelick.drinksy.cocktails.data.Element
import com.makelick.drinksy.reviews.data.Review
import com.makelick.drinksy.reviews.data.ReviewRepository
import com.makelick.drinksy.reviews.data.ReviewWithUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewsViewModel @Inject constructor () : ViewModel() {
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
        }
    }