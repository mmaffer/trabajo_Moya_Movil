package com.tecsup.productmanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.productmanager.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val userId: String? = null
)

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        val user = repository.getCurrentUser()
        _authState.value = AuthState(
            isLoggedIn = user != null,
            userId = user?.uid
        )
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)

            val result = repository.login(email, password)

            if (result.isSuccess) {
                _authState.value = AuthState(
                    isLoggedIn = true,
                    userId = result.getOrNull()?.uid
                )
            } else {
                _authState.value = AuthState(
                    error = result.exceptionOrNull()?.message ?: "Error desconocido"
                )
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)

            val result = repository.register(email, password)

            if (result.isSuccess) {
                _authState.value = AuthState(
                    isLoggedIn = true,
                    userId = result.getOrNull()?.uid
                )
            } else {
                _authState.value = AuthState(
                    error = result.exceptionOrNull()?.message ?: "Error desconocido"
                )
            }
        }
    }

    fun logout() {
        repository.logout()
        _authState.value = AuthState(isLoggedIn = false)
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}