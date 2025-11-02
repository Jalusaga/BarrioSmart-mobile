package com.example.barriosmartfront.data.dto.community
data class Community(
    val id: Int = -1,
    val memberCount: Int = 0,

    val name: String = "",
    val description: String = "",

    val is_active: Boolean = false,
)