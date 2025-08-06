package com.example.codechat.core.utils

import android.content.Context
import androidx.datastore.core.DataStore
// androidx.datastore.dataStore is not used directly here
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// This top-level property should be defined once, typically in a file dedicated to DataStore setup
// or App class, to avoid multiple instances if TokenManager is created multiple times
// (though Hilt @Singleton should prevent multiple TokenManager instances).
// For now, keeping it here as per original.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val tokenDataStore: DataStore<Preferences> = context.dataStore
    companion object { // Using companion object for keys is a common pattern
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    suspend fun saveAuthData(token: String, userId: Int) {
        tokenDataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_ID_KEY] = userId.toString()
        }
    }

    suspend fun saveToken(token: String) {
        tokenDataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun getToken(): String? {
        return tokenDataStore.data
            .map { preferences -> preferences[TOKEN_KEY] }
            .first() // Use firstOrNull() if you want to handle the case where datastore is empty without an exception
    }

    suspend fun getUserId(): String? {
        return tokenDataStore.data
            .map { preferences -> preferences[USER_ID_KEY] }
            .first()
    }

    val tokenFlow: Flow<String?> = tokenDataStore.data.map { preferences -> preferences[TOKEN_KEY] }
    val userIdFlow: Flow<String?> = tokenDataStore.data.map { preferences -> preferences[USER_ID_KEY] }

    suspend fun clearAuthData() {
        tokenDataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
        }
    }
}
