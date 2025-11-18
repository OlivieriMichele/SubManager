package com.example.submanager.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureCredentialManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "credentials_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object{
        private const val KEY_EMAIL = "user_email"
        private const val KEY_PASSWORD = "user_password"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enaboled"
    }

    fun saveCredential(email: String, password: String) {
        sharedPreferences.edit().apply {
            putString(KEY_EMAIL, email)
            putString(KEY_PASSWORD, password)
            apply()
        }
    }

    fun getEmail(): String? {
        return sharedPreferences.getString(KEY_EMAIL, null)
    }

    fun getPassword(): String? {
        return sharedPreferences.getString(KEY_PASSWORD, null)
    }

    fun hasStoreCredentials(): Boolean {
        return getEmail() != null && getPassword() != null
    }

    fun setBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(KEY_BIOMETRIC_ENABLED, enabled)
            apply()
        }
    }

    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    fun clearCredentials() {
        sharedPreferences.edit().apply {
            remove(KEY_EMAIL)
            remove(KEY_PASSWORD)
            remove(KEY_BIOMETRIC_ENABLED)
            apply()
        }
    }
}