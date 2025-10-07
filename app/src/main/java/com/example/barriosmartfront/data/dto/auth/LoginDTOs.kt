package com.example.barriosmartfront.data.dto.auth

import androidx.compose.ui.graphics.vector.ImageVector // Asegúrate de tener esta importación
data class LoginRequest(val email: String, val password: String)
data class TokenResponse(val access_token: String, val token_type: String)

data class NavItem(val title: String, val icon: ImageVector)