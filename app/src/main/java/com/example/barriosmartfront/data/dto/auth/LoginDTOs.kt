package com.example.barriosmartfront.data.dto.auth

data class LoginRequest(val email: String, val password: String)
data class TokenResponse(val access_token: String, val token_type: String)