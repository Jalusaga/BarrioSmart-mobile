package com.example.barriosmartfront.data.dto.member

import kotlinx.serialization.Serializable

@Serializable
data class Member(
    val id: Long? = null,                 // puede ser nulo
    val full_name: String? = null,        // opcional
    val email: String? = null,            // opcional
    val phone: String? = null,            // opcional
    val is_admin: Boolean = false,        // valor por defecto
    val is_active: Boolean = true,        // valor por defecto
    val created_at: String? = null,       // opcional
    val updated_at: String? = null        // opcional
)
