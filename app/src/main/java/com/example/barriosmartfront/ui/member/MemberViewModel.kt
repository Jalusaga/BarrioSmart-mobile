package com.example.barriosmartfront.ui.member

data class Member(
    val initials: String,
    val name: String,
    val joinDate: String,
    val role: String? = null // 'Administrador' o null
)

