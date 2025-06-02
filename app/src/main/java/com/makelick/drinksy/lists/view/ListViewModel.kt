package com.makelick.drinksy.lists.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makelick.drinksy.lists.data.CocktailList
import com.makelick.drinksy.lists.data.CocktailListRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val cocktailListRepo: CocktailListRepo
) : ViewModel() {

    private val _list = MutableStateFlow<CocktailList>(CocktailList())
    val list: StateFlow<CocktailList> = _list

    fun loadCocktailsForList(listId: String) {
        viewModelScope.launch {
            _list.value = cocktailListRepo.getCocktailListById(listId) ?: CocktailList()
        }
    }

    fun updateCocktailFavoriteStatus(cocktailId: String, isFavorite: Boolean) {
        // Implement favorite logic as needed
    }
}
