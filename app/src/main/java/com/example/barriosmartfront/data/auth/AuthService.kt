package com.example.barriosmartfront.data.auth

import com.example.barriosmartfront.data.dto.auth.LoginRequest
import com.example.barriosmartfront.data.dto.auth.RegisterRequest
import com.example.barriosmartfront.data.dto.auth.RegisterResponse
import com.example.barriosmartfront.data.dto.auth.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): TokenResponse
}

interface UsersService {
    @POST("users/")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse
}

