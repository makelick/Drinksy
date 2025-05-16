package com.makelick.drinksy.profile.view

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.makelick.drinksy.profile.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Available tastes to choose from
    val availableTastes = listOf(
        "Sweet", "Sour", "Bitter", "Spicy", "Fruity",
        "Strong", "Citrus", "Herbal", "Exotic", "Refreshing",
        "Creamy", "Smooth", "Fizzy", "Tangy", "Floral"
    )

    init {
        // Initialize with mock data
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val mockUser = User(
            id = "user123",
            name = "John Doe",
            profilePictureUrl = "",
            favoriteCocktails = emptyList(),
            tastes = listOf("Sweet", "Fruity", "Citrus")
        )

        _uiState.value = ProfileUiState(
            userId = mockUser.id,
            username = mockUser.name,
            profilePictureUrl = mockUser.profilePictureUrl,
            selectedTastes = mockUser.tastes.toMutableList(),
            isLoading = false
        )
    }

    fun updateUsername(newName: String) {
        _uiState.value = _uiState.value.copy(username = newName)
        // In a real app, you would update this to the backend
        viewModelScope.launch {
            // Simulate API call
            // userRepository.updateUsername(uiState.value.userId, newName)
        }
    }

    fun updateProfilePicture(imageUri: Uri?) {
        if (imageUri != null) {
            _uiState.value = _uiState.value.copy(profilePictureUri = imageUri)
            // In a real app, you would upload this to Firebase
            viewModelScope.launch {
                // Simulate upload to Firebase
                // val url = firebaseStorage.uploadProfilePicture(uiState.value.userId, imageUri)
                // _uiState.value = _uiState.value.copy(profilePictureUrl = url)
            }
        }
    }

    fun addTaste(taste: String) {
        val currentTastes = _uiState.value.selectedTastes.toMutableList()
        if (!currentTastes.contains(taste)) {
            currentTastes.add(taste)
            _uiState.value = _uiState.value.copy(selectedTastes = currentTastes)
            // In a real app, you would update this to the backend
        }
    }

    fun removeTaste(taste: String) {
        val currentTastes = _uiState.value.selectedTastes.toMutableList()
        currentTastes.remove(taste)
        _uiState.value = _uiState.value.copy(selectedTastes = currentTastes)
        // In a real app, you would update this to the backend
    }
}

// UI state for the profile screen
data class ProfileUiState(
    val userId: String = "",
    val username: String = "",
    val profilePictureUrl: String = "",
    val profilePictureUri: Uri? = null,
    val selectedTastes: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val isEditingUsername: Boolean = false,
    val isSelectingTastes: Boolean = false
)