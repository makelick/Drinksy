package com.makelick.drinksy.cocktails.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makelick.drinksy.cocktails.data.Cocktail
import com.makelick.drinksy.cocktails.data.FirestoreRepository
import com.makelick.drinksy.login.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CocktailViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _cocktail = MutableStateFlow<Cocktail?>(null)
    val cocktail: StateFlow<Cocktail?> = _cocktail

    fun loadCocktail(id: String) {
        viewModelScope.launch {
            _cocktail.value = firestoreRepository.getCocktailById(id)
        }
    }

    fun toggleFavoriteStatus() {
        viewModelScope.launch {
            _cocktail.value?.let { cocktail ->
                val userId = authRepository.getCurrentUser()?.uid
                if (userId != null) {
                    firestoreRepository.toggleFavoriteStatus(cocktail.id, userId)
                    _cocktail.value = cocktail.copy(isFavorite = !cocktail.isFavorite)
                }
            }
        }
    }
}
