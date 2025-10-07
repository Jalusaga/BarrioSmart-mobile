package com.example.barriosmartfront.ui.community

import java.time.LocalDateTime

data class Community(
    val id: Int,
    val name: String,
    val description: String,
    val is_active: Boolean,
    val memberCount: Int = 45, // Simulación para el diseño
    val isJoined: Boolean = false // Simulación para determinar el botón (Unirse/Salir)
)