package com.example.barriosmartfront.data.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1) DataStore "auth_prefs"
val Context.authDataStore by preferencesDataStore("auth_prefs")

// 2) Clave para el token
object AuthKeys {
    val JWT_TOKEN = stringPreferencesKey("jwt_token")
}