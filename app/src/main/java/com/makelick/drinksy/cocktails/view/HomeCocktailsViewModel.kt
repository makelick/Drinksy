package com.makelick.drinksy.cocktails.view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makelick.drinksy.cocktails.data.Cocktail
import com.makelick.drinksy.cocktails.data.FirestoreRepository
import com.makelick.drinksy.cocktails.data.TFLiteRecommender
import com.makelick.drinksy.login.data.AuthRepository
import com.makelick.drinksy.profile.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeCocktailsViewModel @Inject constructor(
//    private val recommendationManager: CocktailRecommendationManager,
    private val recommender: TFLiteRecommender,
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _cocktails = MutableStateFlow<List<Cocktail>>(emptyList())
    val cocktails: StateFlow<List<Cocktail>> = _cocktails.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isFiltersVisible = MutableStateFlow(false)
    val isFiltersVisible: StateFlow<Boolean> = _isFiltersVisible.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _recommendations =
        MutableStateFlow<List<String>>(emptyList()) // List of recommended cocktail IDs

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            getRecommendations()
        }
    }

    suspend fun getRecommendations() {
        _isRefreshing.value = true
        Log.e("HomeCocktailsViewModel", "Refreshing cocktails and recommendations")
        val currentUser = authRepository.getCurrentUser()?.uid?.let {
            authRepository.getUserFromFirestore(it)
        }
        Log.e("HomeCocktailsViewModel", "Current user: $currentUser")
        if (currentUser != null) {
            val recommendedIds = recommender.recommend(currentUser)
            _recommendations.value = recommendedIds.sortedBy { it.second }
                .map { it.first } // Extract only the cocktail IDs
            val allCocktails = firestoreRepository.getAllCocktails()
            val recommendations = allCocktails.filter { cocktail ->
                recommendedIds.any { it.first == cocktail.id }
            }
            _cocktails.value = recommendations
            _isRefreshing.value = false
        }
    }

    init {
        viewModelScope.launch {
            getRecommendations()
        }
    }

//    private suspend fun loadCocktailsAndRecommendations() {
//        val currentUser = authRepository.getCurrentUser()?.uid?.let {
//            authRepository.getUserFromFirestore(it)
//        }
//        if (currentUser != null) {
//            Log.e("HomeCocktailsViewModel", "Current user: ${currentUser.tastes}")
//            val recommendedIds =
//                recommendationManager.getRecommendations(currentUser, 500)
//                    ?.map { it.cocktailId } ?: emptyList()
//            _recommendations.value = recommendedIds
//            val allCocktails = firestoreRepository.getAllCocktails()
//            val sorted = allCocktails.sortedBy { cocktail ->
//                val index = recommendedIds.indexOf(cocktail.id)
//                if (index != -1) {
//                    index
//                } else {
//                    allCocktails.size
//                }
//            }
//            _cocktails.value = sorted
//        } else {
//            val allCocktails = firestoreRepository.getAllCocktails()
//            _cocktails.value = allCocktails
//        }
//    }

    fun updateCocktailFavoriteStatus(id: String, favorite: Boolean) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.uid
            if (userId != null) {
                firestoreRepository.toggleFavoriteStatus(id, userId)
            }
            _cocktails.value = _cocktails.value.map { cocktail ->
                if (cocktail.id == id) {
                    cocktail.copy(isFavorite = favorite)
                } else {
                    cocktail
                }

            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        // Optionally filter cocktails here
    }

    fun toggleFiltersVisibility() {
        _isFiltersVisible.value = !_isFiltersVisible.value
    }

    fun searchCocktails() {
        // Optionally implement search logic here
    }

//    fun toggleFavoriteStatus(cocktailId: String) {
//        viewModelScope.launch {
//
//        }
//    }
}
