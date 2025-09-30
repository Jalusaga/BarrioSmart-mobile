package com.example.barriosmartfront.data.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


class DataStoreTokenStore(
    private val appContext: Context
) : ITokenStore {

    override val tokenFlow: Flow<String?> =
        appContext.authDataStore.data.map { it[AuthKeys.JWT_TOKEN] }

    override suspend fun save(token: String) {
        appContext.authDataStore.edit { prefs ->
            prefs[AuthKeys.JWT_TOKEN] = token
        }
    }

    override suspend fun clear() {
        appContext.authDataStore.edit { prefs ->
            prefs.remove(AuthKeys.JWT_TOKEN)
        }
    }

    override suspend fun current(): String? = tokenFlow.first()
}