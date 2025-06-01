package com.makelick.drinksy.profile.view

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makelick.drinksy.login.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    val availableTastes = listOf(
        "Sweet", "Sour", "Bitter", "Spicy", "Fruity",
        "Strong", "Citrus", "Herbal", "Exotic", "Refreshing",
        "Creamy", "Smooth", "Fizzy", "Tangy", "Floral"
    )

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val user = authRepository.getCurrentUser()
            if (user != null) {
                val firebaseUser = authRepository.getUserFromFirestore(user.uid)
                _uiState.value = ProfileUiState(
                    userId = firebaseUser.id,
                    username = firebaseUser.name,
                    profilePictureUrl = firebaseUser.profilePictureUrl,
                    selectedTastes = firebaseUser.tastes.toMutableList(),
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun updateUsername(newName: String) {
        _uiState.value = _uiState.value.copy(username = newName)
        viewModelScope.launch {
            authRepository.updateUsername(_uiState.value.userId, newName)
        }
    }

    fun updateProfilePicture(imageUri: Uri?) {
        if (imageUri != null) {
            _uiState.value = _uiState.value.copy(profilePictureUri = imageUri)
            viewModelScope.launch {
                val url = authRepository.uploadProfilePicture(_uiState.value.userId, imageUri)
                authRepository.updatePictureUrl(_uiState.value.userId, url)
                _uiState.value = _uiState.value.copy(profilePictureUrl = url)
            }
        }
    }

    fun addTaste(taste: String) {
        val currentTastes = _uiState.value.selectedTastes.toMutableList()
        if (!currentTastes.contains(taste)) {
            currentTastes.add(taste)
            _uiState.value = _uiState.value.copy(selectedTastes = currentTastes)
            viewModelScope.launch {
                authRepository.updateTastes(_uiState.value.userId, currentTastes)
            }
        }
    }

    fun removeTaste(taste: String) {
        val currentTastes = _uiState.value.selectedTastes.toMutableList()
        currentTastes.remove(taste)
        _uiState.value = _uiState.value.copy(selectedTastes = currentTastes)
        viewModelScope.launch {
            authRepository.updateTastes(_uiState.value.userId, currentTastes)
        }
    }

    fun signOut() {
        authRepository.signOut()
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