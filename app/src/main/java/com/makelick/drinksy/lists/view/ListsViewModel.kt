package com.makelick.drinksy.lists.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makelick.drinksy.lists.data.CocktailList
import com.makelick.drinksy.lists.data.CocktailListRepo
import com.makelick.drinksy.lists.data.ListType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListsViewModel @Inject constructor(
    private val cocktailListRepository: CocktailListRepo,
) : ViewModel() {
    // State
    val cocktailLists = MutableStateFlow<List<CocktailList>>(emptyList())
    val searchQuery = MutableStateFlow("")
    val isLoading = MutableStateFlow(false)
    val showFilters = MutableStateFlow(false)
    val selectedListType = MutableStateFlow<ListType?>(null)
    val showOnlyMine = MutableStateFlow(false)

    init {
        fetchCocktailsLists()
    }

    // Actions
    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun searchLists() {
        // Implementation would call repository to search lists
        isLoading.value = true
        // Simulate API call
        viewModelScope.launch {
            delay(500)
            // Update lists based on search
            isLoading.value = false
        }
    }

    fun toggleFilterVisibility() {
        showFilters.value = !showFilters.value
    }

    fun updateListType(type: ListType?) {
        selectedListType.value = type
    }

    fun updateShowOnlyMine(showMine: Boolean) {
        showOnlyMine.value = showMine
    }

    fun applyFilters() {
        // Implementation would apply filters and update list
        isLoading.value = true
        // Simulate API call
        viewModelScope.launch {
            delay(500)
            // Update lists based on filters
            isLoading.value = false
        }
    }

    private fun fetchCocktailsLists() {
        viewModelScope.launch {
            isLoading.value = true
            cocktailLists.value = cocktailListRepository.getPublicCocktailLists()
            isLoading.value = false
        }
    }
}