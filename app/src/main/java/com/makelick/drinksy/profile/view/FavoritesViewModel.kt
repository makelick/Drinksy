package com.makelick.drinksy.profile.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makelick.drinksy.cocktails.data.Cocktail
import com.makelick.drinksy.login.data.AuthRepository
import com.makelick.drinksy.profile.data.CocktailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val cocktailRepository: CocktailRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val cocktails: StateFlow<List<Cocktail>>
        get() = _cocktails

    private val _cocktails = MutableStateFlow<List<Cocktail>>(emptyList())

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                val favorites = cocktailRepository.getFavoriteCocktails(user.uid)
                _cocktails.value = favorites
            } else {
                _cocktails.value = emptyList()
            }
        }
    }
}
