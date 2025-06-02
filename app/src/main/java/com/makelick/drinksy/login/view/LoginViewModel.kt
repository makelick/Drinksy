package com.makelick.drinksy.login.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makelick.drinksy.login.data.AuthRepository
import com.makelick.drinksy.profile.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _authMode = MutableStateFlow(AuthMode.SIGN_IN)
    val authMode: StateFlow<AuthMode> = _authMode.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                try {
                    // Get user data from Firestore
                    val userDoc = authRepository.getUserFromFirestore(currentUser.uid)
                    _authState.value = AuthState.Authenticated(userDoc)
                } catch (e: Exception) {
                    _authState.value = AuthState.Error(e.message ?: "Unknown error")
                }
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun updateEmail(email: String) {
        _email.value = email
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun updateName(name: String) {
        _name.value = name
    }

    fun toggleAuthMode() {
        _authMode.value =
            if (_authMode.value == AuthMode.SIGN_IN) AuthMode.SIGN_UP else AuthMode.SIGN_IN
        clearError()
    }

    fun signInWithEmail() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.signInWithEmail(_email.value, _password.value)
            handleAuthResult(result)
            _isLoading.value = false
        }
    }

    fun signUpWithEmail() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.signUpWithEmail(_email.value, _password.value, _name.value)
            handleAuthResult(result)
            _isLoading.value = false
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.signInWithGoogle(idToken)
            handleAuthResult(result)
            _isLoading.value = false
        }
    }

    private fun handleAuthResult(result: Result<User>) {
        result.onSuccess { user ->
            _authState.value = AuthState.Authenticated(user)
        }.onFailure { exception ->
            _authState.value = AuthState.Error(exception.message ?: "Authentication failed")
        }
    }

    private fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }
}

sealed class AuthState {
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

enum class AuthMode {
    SIGN_IN, SIGN_UP
}
