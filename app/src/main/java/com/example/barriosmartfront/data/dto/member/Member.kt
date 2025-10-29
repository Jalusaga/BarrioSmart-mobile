package com.example.barriosmartfront.data.dto.member

import kotlinx.serialization.Serializable

@Serializable
data class Member(
    val id: Long,             // ID del usuario
    val initials: String,     // Iniciales o avatar
    val fullName: String,     // Nombre completo
    val joinedAt: String,     // Fecha de ingreso a la comunidad
    val role: String? = null  // Opcional: rol dentro de la comunidad
)
