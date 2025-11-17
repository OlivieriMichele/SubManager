package com.example.submanager.data.repositories

import com.example.submanager.utils.SecureCredentialManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.tasks.await

data class AuthResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

class AuthRepository(
    private val auth: FirebaseAuth,
    private val credentialsManager: SecureCredentialManager
) {
    suspend fun login(email: String, password: String, saveBiometric: Boolean): AuthResult {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            if (saveBiometric){
                credentialsManager.saveCredential(email, password)
                credentialsManager.setBiometricEnabled(true)
            }
            AuthResult(isSuccess = true)
        } catch (e: FirebaseAuthException) {
            AuthResult(isSuccess = false, errorMessage = mapError(e))
        } catch (e: Exception) {
            AuthResult(isSuccess = false, errorMessage = "Errore di connessione")
        }
    }

    suspend fun register(email: String, password: String): AuthResult {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            AuthResult(isSuccess = true)
        } catch (e: FirebaseAuthException) {
            AuthResult(isSuccess = false, errorMessage = mapError(e))
        } catch (e: Exception) {
            AuthResult(isSuccess = false, errorMessage = "Errore di connessione")
        }
    }

    suspend fun logout() {
        auth.signOut()
    }

    suspend fun loginWithBiometric(): Result<Boolean> {
        return try {
            val email = credentialsManager.getEmail()
            val password = credentialsManager.getPassword()
            if (email == null || password == null)
                return Result.failure(Exception("Credenziali non salvate"))

            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun hasStoredCredentials(): Boolean {
        return credentialsManager.hasStoreCredentials()
    }

    fun getStoredEmail(): String? {
        return credentialsManager.getEmail()
    }

    fun isBiometricEnabled(): Boolean {
        return credentialsManager.isBiometricEnabled()
    }

    fun disableBiometric() {
        credentialsManager.clearCredentials()
    }

    fun getCurrentUser() = auth.currentUser

    suspend fun isAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    private fun mapError(e: FirebaseAuthException): String {
        return when (e.errorCode) {
            "ERROR_INVALID_EMAIL" -> "Email non valida"
            "ERROR_WRONG_PASSWORD" -> "Password errata"
            "ERROR_USER_NOT_FOUND" -> "Utente non trovato"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Email già registrata"
            "ERROR_WEAK_PASSWORD" -> "Password troppo debole"
            "ERROR_TOO_MANY_REQUESTS" -> "Troppi tentativi"
            else -> e.localizedMessage ?: "Errore sconosciuto"
        }
    }
}