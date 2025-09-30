package com.example.barriosmartfront.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barriosmartfront.data.AuthRepository
import com.example.barriosmartfront.data.auth.AuthService
import com.example.barriosmartfront.data.auth.ITokenStore
import com.example.barriosmartfront.data.auth.LoginRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ---- ViewModel (estado simple) ----
class LoginViewModel(
    private val repo: AuthRepository
) : ViewModel() {

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun updateEmail(v: String) { email = v }
    fun updatePassword(v: String) { password = v }

    fun doLogin(onSuccess: () -> Unit) = viewModelScope.launch {
        error = null
        isLoading = true
        val e = email.trim()
        val p = password
        if (e.isBlank() || p.isBlank()) {
            error = "Ingresa correo y contraseña."
            isLoading = false
            return@launch
        }
        val res = repo.login(e, p)
        isLoading = false
        res.onSuccess { onSuccess() }
            .onFailure { error = it.message ?: "No se pudo iniciar sesión." }
    }
}
