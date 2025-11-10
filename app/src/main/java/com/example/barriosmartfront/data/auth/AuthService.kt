package com.example.barriosmartfront.data.auth

import com.example.barriosmartfront.data.dto.auth.LoginRequest
import com.example.barriosmartfront.data.dto.auth.RegisterRequest
import com.example.barriosmartfront.data.dto.auth.RegisterResponse
import com.example.barriosmartfront.data.dto.auth.TokenResponse
import com.example.barriosmartfront.data.dto.community.Community
import com.example.barriosmartfront.data.dto.member.Member
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): TokenResponse
}

interface UsersService {
    @POST("users/")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    @GET("users/me/{email}")
    suspend fun getCurrentUser(
        @Path("email") email: String
    ): Member

}

