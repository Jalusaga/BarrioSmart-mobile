package com.example.barriosmartfront.ui.authorities

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel

data class Authority(
    val title: String,
    val phone: String,
    val description: String,
    val availability: String,
    val backgroundColor: Color,
    val icon: ImageVector,
    val iconColor: Color
)

class AuthoritiesViewModel(
    //private val repo: AuthRepository
) : ViewModel() {

}