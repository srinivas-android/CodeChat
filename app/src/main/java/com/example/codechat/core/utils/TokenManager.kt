package com.example.codechat.core.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class TokenManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val tokenDataStore: DataStore<Preferences> = context.dataStore
    private val TOKEN_KEY = stringPreferencesKey("auth_token")

    suspend fun saveToken(token: String) {
        tokenDataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun getToken(): String? {
        return tokenDataStore.data
            .map { it -> it[TOKEN_KEY] }
            .first()
    }

    val tokenFlow: Flow<String?> = tokenDataStore.data.map { it[TOKEN_KEY] }
}