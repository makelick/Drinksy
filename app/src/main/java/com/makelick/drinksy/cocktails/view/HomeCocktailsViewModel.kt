package com.makelick.drinksy.cocktails.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makelick.drinksy.cocktails.data.Cocktail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeCocktailsViewModel @Inject constructor() : ViewModel() {
    val cocktails: StateFlow<List<Cocktail>>
        get() = _cocktails

    private val _cocktails = MutableStateFlow<List<Cocktail>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _isFiltersVisible = MutableStateFlow(false)

    val searchQuery: StateFlow<String> = _searchQuery
    val isFiltersVisible: StateFlow<Boolean> = _isFiltersVisible

    init {
        // In a real app, this would come from a repository
        loadCocktails()
    }

    private fun loadCocktails() {
        viewModelScope.launch {
            _cocktails.value = generateSampleCocktails()
        }
    }

    fun updateCocktailFavoriteStatus(id: String, favorite: Boolean) {
        viewModelScope.launch {
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
        // In a real app, this would filter the cocktails based on search query
    }

    fun toggleFiltersVisibility() {
        _isFiltersVisible.value = !_isFiltersVisible.value
    }

    fun searchCocktails() {
        // Perform search based on current query
        // In a real app, this would filter from the repository
    }

    private fun generateSampleCocktails(): List<Cocktail> {
        return listOf(
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
                ingredients = listOf("Vodka", "Triple Sec", "Cranberry Juice", "Lime Juice"),
                instructions = "Shake vodka, triple sec, cranberry juice, and lime juice with ice. Strain into a glass.",
                category = "Cocktail",
                rating = 4.1f,
                reviews = emptyList(),
                isFavorite = false
            )
        )
    }
}
