package com.makelick.drinksy.lists.view

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makelick.drinksy.cocktails.data.Cocktail
import com.makelick.drinksy.lists.data.CocktailList
import com.makelick.drinksy.lists.data.CocktailListRepo
import com.makelick.drinksy.lists.data.CocktailRepo
import com.makelick.drinksy.lists.data.CocktailWithPrice
import com.makelick.drinksy.lists.data.ListType
import com.makelick.drinksy.login.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CocktailListEditorUiState(
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val isPublic: Boolean = false,
    val type: ListType = ListType.COMPILATION,
    val cocktailsWithPrice: List<CocktailWithPrice> = emptyList(),
    val isUploading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CocktailListEditorViewModel @Inject constructor(
    private val cocktailRepository: CocktailRepo,
    private val cocktailListRepository: CocktailListRepo,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _listId = MutableStateFlow<String?>(null)

    private val _uiState = MutableStateFlow(CocktailListEditorUiState())
    val uiState: StateFlow<CocktailListEditorUiState> = _uiState.asStateFlow()

    private val _allCocktails = MutableStateFlow<List<Cocktail>>(emptyList())
    val allCocktails: StateFlow<List<Cocktail>> = _allCocktails.asStateFlow()

    init {
        setListId(null)
    }

    fun setListId(id: String?) {
        _listId.value = id
        loadAllCocktails()
        loadCocktailList(_listId.value.toString())
    }

    private fun loadAllCocktails() {
        viewModelScope.launch {
            try {
                _allCocktails.value = cocktailRepository.getAllCocktails()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    private fun loadCocktailList(id: String) {
        viewModelScope.launch {
            try {
                val cocktailList = cocktailListRepository.getCocktailListById(id)
                cocktailList?.let {
                    _uiState.value = _uiState.value.copy(
                        name = it.name,
                        description = it.description,
                        imageUrl = it.imageUrl,
                        isPublic = it.isPublic,
                        type = it.type,
                        cocktailsWithPrice = it.cocktails.map { cocktail ->
                            CocktailWithPrice(cocktail = cocktail)
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun updateIsPublic(isPublic: Boolean) {
        _uiState.value = _uiState.value.copy(isPublic = isPublic)
    }

    fun updateType(type: ListType) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isUploading = true)
                val imageUrl = cocktailRepository.uploadImage(uri)
                _uiState.value = _uiState.value.copy(
                    imageUrl = imageUrl,
                    isUploading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isUploading = false
                )
            }
        }
    }

    fun addCocktail(cocktail: Cocktail) {
        val currentList = _uiState.value.cocktailsWithPrice.toMutableList()
        if (!currentList.any { it.cocktail.id == cocktail.id }) {
            currentList.add(CocktailWithPrice(cocktail = cocktail))
            _uiState.value = _uiState.value.copy(cocktailsWithPrice = currentList)
        }
    }

    fun removeCocktail(cocktailId: String) {
        val currentList = _uiState.value.cocktailsWithPrice.toMutableList()
        currentList.removeAll { it.cocktail.id == cocktailId }
        _uiState.value = _uiState.value.copy(cocktailsWithPrice = currentList)
    }

    fun updateCocktailPrice(cocktailId: String, price: String) {
        val currentList = _uiState.value.cocktailsWithPrice.toMutableList()
        val index = currentList.indexOfFirst { it.cocktail.id == cocktailId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(price = price)
            _uiState.value = _uiState.value.copy(cocktailsWithPrice = currentList)
        }
    }

    fun updateCocktailComment(cocktailId: String, comment: String) {
        val currentList = _uiState.value.cocktailsWithPrice.toMutableList()
        val index = currentList.indexOfFirst { it.cocktail.id == cocktailId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(comment = comment)
            _uiState.value = _uiState.value.copy(cocktailsWithPrice = currentList)
        }
    }

    fun saveCocktailList() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true)

                val cocktailList = CocktailList(
                    id = _listId.value.toString(),
                    name = _uiState.value.name,
                    description = _uiState.value.description,
                    imageUrl = _uiState.value.imageUrl.ifEmpty {
                        generateCollageUrl(_uiState.value.cocktailsWithPrice.map { it.cocktail })
                    },
                    type = _uiState.value.type,
                    cocktails = _uiState.value.cocktailsWithPrice.map { it.cocktail },
                    isPublic = _uiState.value.isPublic,
                    creatorUser = authRepository.getCurrentUser()?.uid ?: ""
                )

                if (_listId.value != null && _listId.value?.isNotEmpty() == true) {
                    cocktailListRepository.updateCocktailList(cocktailList)
                } else {
                    cocktailListRepository.saveCocktailList(cocktailList)
                }

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    isSaved = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isSaving = false
                )
                Log.e("CocktailListEditor", "Error saving cocktail list: ${_listId.value}")
                e.printStackTrace()
            }
        }
    }

    private fun generateCollageUrl(cocktails: List<Cocktail>): String {
        // This would generate a collage from 1-4 cocktail images
        // Implementation depends on your image processing service
        return ""
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}