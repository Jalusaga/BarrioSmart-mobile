// data/network/dto/RegisterDTOs.kt
package com.example.barriosmartfront.data.network.dto

data class RegisterRequest(
    val full_name: String,
    val email: String,
    val phone: String?,   // nullable/optional
    val password: String,
    val is_admin: Boolean,
    val is_active: Boolean,// server will hash into password_hash
)

data class RegisterResponse(
    val id: Long,
    val full_name: String,
    val email: String,
    val phone: String,
    val is_admin: Boolean,
    val is_active: Boolean,
    val created_at: String?,
    val updated_at: String?
)
