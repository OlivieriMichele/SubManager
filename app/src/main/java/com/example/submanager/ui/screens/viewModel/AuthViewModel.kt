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
    val rememberMe: Boolean = false, // ✅ Semplificato: un solo flag
    val canUseBiometric: Boolean = false // ✅ Combinazione di hasSavedCredentials + biometricAvailable
)

interface AuthActions {
    fun updateEmail(email: String)
    fun updatePassword(password: String)
    fun updateConfirmPassword(password: String)
    fun login()
    fun register()
    fun logout()
    fun checkAuth()
    fun loginWithBiometric()
    fun toggleRememberMe()
    fun checkBiometricAvailability(isAvailable: Boolean)
    fun onBiometricResult(result: BiometricAuthManager.BiometricResult)
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
        val hasCredentials = repository.hasStoredCredentials()
        val storedEmail = repository.getStoredEmail()
        val biometricEnabled = repository.isBiometricEnabled()

        _state.update {
            it.copy(
                email = storedEmail ?: "",
                rememberMe = biometricEnabled,
                // canUseBiometric sarà aggiornato quando checkBiometricAvailability viene chiamato
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
                _state.update { it.copy(isAuthenticated = false) }
            }
        }

        override fun checkAuth() {
            viewModelScope.launch {
                val isAuth = repository.isAuthenticated()
                _state.update { it.copy(isAuthenticated = isAuth) }
            }
        }

        override fun loginWithBiometric() {
            // La UI mostrerà il prompt biometrico nativo
        }

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
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                    is BiometricAuthManager.BiometricResult.Failed -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Impronta non riconosciuta"
                            )
                        }
                    }
                    is BiometricAuthManager.BiometricResult.Unavailable -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Biometria non disponibile"
                            )
                        }
                    }
                }
            }
        }
    }

    fun disableBiometric() {
        repository.disableBiometric()
        _state.update {
            it.copy(
                canUseBiometric = false,
                rememberMe = false
            )
        }
    }
}