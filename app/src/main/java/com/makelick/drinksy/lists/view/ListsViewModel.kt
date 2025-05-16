package com.makelick.drinksy.lists.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makelick.drinksy.cocktails.data.Cocktail
import com.makelick.drinksy.lists.data.CocktailList
import com.makelick.drinksy.lists.data.ListType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListsViewModel @Inject constructor() : ViewModel() {
    // State
    val cocktailLists = MutableStateFlow<List<CocktailList>>(emptyList())
    val searchQuery = MutableStateFlow("")
    val isLoading = MutableStateFlow(false)
    val showFilters = MutableStateFlow(false)
    val selectedListType = MutableStateFlow<ListType?>(null)
    val showOnlyMine = MutableStateFlow(false)

    init {
        cocktailLists.value = fetchCocktailsLists()
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

    fun getCreatorName(userId: String): String {
        // In real implementation, this would come from user repository
        return "User $userId"
    }

    private fun fetchCocktailsLists(): List<CocktailList> {
        // Simulate fetching cocktail lists from a repository
        return List(2) {
            CocktailList(
                id = "1",
                name = "Summer Cocktails",
                imageUrl = "https://www.onlyfoods.net/wp-content/uploads/2017/09/Summer-Drinks.jpg",
                description = "Refreshing cocktails for summer",
                type = ListType.MENU,
                cocktails = listOf(
                    Cocktail(
                        id = "1",
                        name = "Mojito",
                        description = "A refreshing cocktail with mint and lime.",
                        imageUrl = "https://images.immediate.co.uk/production/volatile/sites/30/2022/06/Tequila-sunrise-fb8b3ab.jpg",
                        ingredients = listOf("Mint", "Lime", "Rum", "Sugar", "Soda Water"),
                        instructions = "Muddle mint and lime, add rum and sugar, top with soda water.",
                        category = "Cocktail",
                        rating = 4.5f,
                        reviews = emptyList(),
                        isFavorite = false
                    ),
                    Cocktail(
                        id = "2",
                        name = "Margarita",
                        description = "A classic cocktail with tequila and lime.",
                        imageUrl = "https://images.immediate.co.uk/production/volatile/sites/30/2020/01/retro-cocktails-b12b00d.jpg?quality=90&resize=556,505",
                        ingredients = listOf("Tequila", "Lime", "Triple Sec", "Salt"),
                        instructions = "Shake tequila, lime, and triple sec with ice. Serve in a salt-rimmed glass.",
                        category = "Cocktail",
                        rating = 4.0f,
                        reviews = emptyList(),
                        isFavorite = false
                    ),
                    Cocktail(
                        id = "3",
                        name = "Old Fashioned",
                        description = "A classic whiskey cocktail.",
                        imageUrl = "https://www.cocktailmag.fr/media/k2/items/cache/da89514e409822180ac867ab6712269d_M.jpg",
                        ingredients = listOf("Whiskey", "Sugar", "Bitters", "Orange Peel"),
                        instructions = "Muddle sugar and bitters, add whiskey, stir with ice, garnish with orange peel.",
                        category = "Cocktail",
                        rating = 4.2f,
                        reviews = emptyList(),
                        isFavorite = false
                    ),
                    Cocktail(
                        id = "4",
                        name = "Pina Colada",
                        description = "A tropical cocktail with rum and pineapple.",
                        imageUrl = "https://www.liquor.com/thmb/f9kQdriaiMZWRylBrRvS4i9IpPs=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/fresh-berry-delicious-720x720-primary-1a5eae4fde84429a98381986abfe2572.jpg",
                        ingredients = listOf("Rum", "Pineapple Juice", "Coconut Cream"),
                        instructions = "Blend rum, pineapple juice, and coconut cream with ice.",
                        category = "Cocktail",
                        rating = 4.3f,
                        reviews = emptyList(),
                        isFavorite = false
                    ),
                    Cocktail(
                        id = "5",
                        name = "Cosmopolitan",
                        description = "A stylish cocktail with vodka and cranberry.",
                        imageUrl = "https://images.immediate.co.uk/production/volatile/sites/2/2020/02/Cocktail-3-bb1c21e.jpg?quality=90&crop=242px,317px,1907px,820px&resize=556,505",
                        ingredients = listOf(
                            "Vodka",
                            "Triple Sec",
                            "Cranberry Juice",
                            "Lime Juice"
                        ),
                        instructions = "Shake vodka, triple sec, cranberry juice, and lime juice with ice. Strain into a glass.",
                        category = "Cocktail",
                        rating = 4.1f,
                        reviews = emptyList(),
                        isFavorite = false
                    )
                ),
                creatorUserId = "1",
                isPublic = true,
            )
        }
    }
}