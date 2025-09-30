package com.example.barriosmartfront.data.auth
import kotlinx.coroutines.flow.Flow


interface ITokenStore {
    val tokenFlow: Flow<String?>
    suspend fun save(token: String)
    suspend fun clear()
    suspend fun current(): String?
}


