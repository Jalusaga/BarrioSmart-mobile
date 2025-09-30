package com.example.barriosmartfront.data.auth

import com.example.barriosmartfront.data.network.dto.RegisterRequest
import com.example.barriosmartfront.data.network.dto.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): TokenResponse
}

interface UsersService {
    @POST("users/")
    suspend fun register(@Body body: RegisterRequest): UserResponse
}

data class LoginRequest(val email: String, val password: String)
data class TokenResponse(val access_token: String, val token_type: String)

data class RegisterRequest(
    val full_name: String,
    val email: String,
    val phone: String?,
    val password: String,
    val is_admin: Boolean = false,
    val is_active: Boolean = true
)

// Alineado al backend (UserResponse de FastAPI)
data class UserResponse(
    val id: Long,
    val full_name: String,
    val email: String?,
    val phone: String?,
    val is_admin: Boolean,
    val is_active: Boolean,
    val created_at: String,
    val updated_at: String
)