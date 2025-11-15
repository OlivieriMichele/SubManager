package com.example.submanager.ui.screens.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submanager.data.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)

interface AuthActions {
    fun updateEmail(email: String)
    fun updatePassword(password: String)
    fun updateConfirmPassword(password: String)
    fun login()
    fun register()
    fun logout()
    fun checkAuth()
}

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    val actions = object : AuthActions {

        override fun updateEmail(email: String) {
            _state.update { it.copy(email = email, error = null) }
        }

        override fun updatePassword(password: String) {
            _state.update { it.copy(password = password, error = null) }
        }

        override fun updateConfirmPassword(password: String) {
            _state.update { it.copy(confirmPassword = password, error = null) }
        }

        override fun login() {
            viewModelScope.launch {
                val current = _state.value

                when {
                    current.email.isBlank() -> {
                        _state.update { it.copy(error = "Inserisci l'email") }
                        return@launch
                    }
                    !current.email.contains("@") -> {
                        _state.update { it.copy(error = "Email non valida") }
                        return@launch
                    }
                    current.password.isBlank() -> {
                        _state.update { it.copy(error = "Inserisci la password") }
                        return@launch
                    }
                }

                _state.update { it.copy(isLoading = true, error = null) }

                val result = repository.login(current.email, current.password)

                _state.update {
                    if (result.isSuccess) {
                        it.copy(
                            isAuthenticated = true,
                            isLoading = false,
                            password = ""
                        )
                    } else {
                        it.copy(
                            isLoading = false,
                            error = result.errorMessage
                        )
                    }
                }
            }
        }

        override fun register() {
            viewModelScope.launch {
                val current = _state.value

                when {
                    current.email.isBlank() -> {
                        _state.update { it.copy(error = "Inserisci l'email") }
                        return@launch
                    }
                    !current.email.contains("@") -> {
                        _state.update { it.copy(error = "Email non valida") }
                        return@launch
                    }
                    current.password.length < 6 -> {
                        _state.update { it.copy(error = "Password minimo 6 caratteri") }
                        return@launch
                    }
                    current.password != current.confirmPassword -> {
                        _state.update { it.copy(error = "Le password non coincidono") }
                        return@launch
                    }
                }

                _state.update { it.copy(isLoading = true, error = null) }

                val result = repository.register(current.email, current.password)

                _state.update {
                    if (result.isSuccess) {
                        it.copy(
                            isAuthenticated = true,
                            isLoading = false,
                            password = "",
                            confirmPassword = ""
                        )
                    } else {
                        it.copy(
                            isLoading = false,
                            error = result.errorMessage
                        )
                    }
                }
            }
        }

        override fun logout() {
            viewModelScope.launch {
                repository.logout()
                _state.update { AuthState() }
            }
        }

        override fun checkAuth() {
            viewModelScope.launch {
                val isAuth = repository.isAuthenticated()
                _state.update { it.copy(isAuthenticated = isAuth) }
            }
        }
    }
}