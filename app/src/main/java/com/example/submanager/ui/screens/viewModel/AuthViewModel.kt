package com.example.submanager.ui.screens.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submanager.data.repositories.AuthRepository
import com.example.submanager.utils.BiometricAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val rememberMe: Boolean = false,
    val canUseBiometric: Boolean = false
)

interface AuthActions {
    fun updateEmail(email: String)
    fun updatePassword(password: String)
    fun updateConfirmPassword(password: String)
    fun login()
    fun register()
    fun logout()
    fun loginWithBiometric()
    fun toggleRememberMe()
    fun checkBiometricAvailability(isAvailable: Boolean)
    fun onBiometricResult(result: BiometricAuthManager.BiometricResult)
    fun disableBiometric()
}

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        loadSavedCredentials()
    }

    private fun loadSavedCredentials() {
        val storedEmail = repository.getStoredEmail()
        val biometricEnabled = repository.isBiometricEnabled()

        _state.update {
            it.copy(
                email = storedEmail ?: "",
                rememberMe = biometricEnabled
            )
        }
    }

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

        override fun toggleRememberMe() {
            _state.update { it.copy(rememberMe = !it.rememberMe) }
        }

        override fun checkBiometricAvailability(isAvailable: Boolean) {
            val hasCredentials = repository.hasStoredCredentials()
            _state.update {
                it.copy(canUseBiometric = isAvailable && hasCredentials)
            }
        }

        override fun login() {
            viewModelScope.launch {
                val current = _state.value

                val validationError = validateLoginInput(current)
                if (validationError != null) {
                    _state.update { it.copy(error = validationError) }
                    return@launch
                }

                _state.update { it.copy(isLoading = true, error = null) }

                val result = repository.login(
                    current.email,
                    current.password,
                    saveBiometric = current.rememberMe
                )

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

                if (result.isSuccess) {
                    loadSavedCredentials()
                }
            }
        }

        override fun register() {
            viewModelScope.launch {
                val current = _state.value

                val validationError = validateRegisterInput(current)
                if (validationError != null) {
                    _state.update { it.copy(error = validationError) }
                    return@launch
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
                _state.update {
                    it.copy(
                        isAuthenticated = false,
                        password = "",
                        confirmPassword = ""
                    )
                }
            }
        }

        override fun loginWithBiometric() {
            // Il prompt biometrico viene gestito dalla UI
        } // todo elimina

        override fun onBiometricResult(result: BiometricAuthManager.BiometricResult) {
            viewModelScope.launch {
                when (result) {
                    is BiometricAuthManager.BiometricResult.Success -> {
                        _state.update { it.copy(isLoading = true, error = null) }

                        val loginResult = repository.loginWithBiometric()

                        _state.update {
                            if (loginResult.isSuccess) {
                                it.copy(
                                    isLoading = false,
                                    isAuthenticated = true,
                                    error = null
                                )
                            } else {
                                it.copy(
                                    isLoading = false,
                                    error = "Login fallito"
                                )
                            }
                        }
                    }
                    is BiometricAuthManager.BiometricResult.Error -> {
                        _state.update { it.copy(error = result.message) }
                    }
                    is BiometricAuthManager.BiometricResult.Failed -> {
                        _state.update { it.copy(error = "Impronta non riconosciuta") }
                    }
                    is BiometricAuthManager.BiometricResult.Unavailable -> {
                        _state.update { it.copy(error = "Biometria non disponibile") }
                    }
                }
            }
        }

        override fun disableBiometric() {
            repository.disableBiometric()
            _state.update {
                it.copy(
                    canUseBiometric = false,
                    rememberMe = false
                )
            }
        }
    }

    private fun validateLoginInput(state: AuthState): String? {
        return when {
            state.email.isBlank() -> "Inserisci l'email"
            !state.email.contains("@") -> "Email non valida"
            state.password.isBlank() -> "Inserisci la password"
            else -> null
        }
    }

    private fun validateRegisterInput(state: AuthState): String? {
        return when {
            state.email.isBlank() -> "Inserisci l'email"
            !state.email.contains("@") -> "Email non valida"
            state.password.length < 6 -> "Password minimo 6 caratteri"
            state.password != state.confirmPassword -> "Le password non coincidono"
            else -> null
        }
    }
}