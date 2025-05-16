package com.makelick.drinksy.cocktails.view

import androidx.lifecycle.ViewModel
import com.makelick.drinksy.cocktails.data.Cocktail
import com.makelick.drinksy.cocktails.data.Element
import com.makelick.drinksy.reviews.data.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.sql.Date
import javax.inject.Inject

@HiltViewModel
class CocktailViewModel @Inject constructor() : ViewModel() {

    private val _cocktail = MutableStateFlow<Cocktail?>(null)
    val cocktail: StateFlow<Cocktail?> = _cocktail

    fun loadCocktail(id: String) {
        _cocktail.value = Cocktail(
            id = id,
            name = "Mojito",
            imageUrl = "https://images.immediate.co.uk/production/volatile/sites/30/2022/06/Tequila-sunrise-fb8b3ab.jpg",
            description = "A refreshing cocktail made with rum, mint, and lime.",
            ingredients = listOf("Rum", "Mint", "Lime", "Sugar", "Soda Water"),
            isFavorite = false,
            instructions = "Mix all ingredients in a glass and serve with ice.",
            category = "Cocktail",
            rating = 4f,
            reviews = listOf(
                Review(
                    id = "1",
                    userId = "user123",
                    rating = 5f,
                    comment = "Delicious and refreshing!",
                    date = Date(System.currentTimeMillis())
                ),
                Review(
                    id = "2",
                    userId = "user122",
                    rating = 4f,
                    comment = "Great for summer parties.",
                    date = Date(System.currentTimeMillis())
                )
            )
        )
    }

    fun toggleFavoriteStatus() {
        _cocktail.value?.let { cocktail ->
            _cocktail.value = cocktail.copy(isFavorite = !cocktail.isFavorite)
            // In a real app, this would update a repository
        }
    }

    private val _element = MutableStateFlow<Element?>(null)
    val element: StateFlow<Element?> = _element.asStateFlow()
}
