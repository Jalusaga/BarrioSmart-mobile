package com.example.barriosmartfront.ui.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barriosmartfront.data.repositories.AuthRepository
import kotlinx.coroutines.launch

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirm: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class RegisterViewModel(
    private val repo: AuthRepository
) : ViewModel() {

    var state = mutableStateOf(RegisterUiState())
        private set

    fun updateFullName(v: String) {
        state.value = state.value.copy(fullName = v)
    }

    fun updateEmail(v: String) {
        state.value = state.value.copy(email = v)
    }

    fun updatePhone(v: String) {
        state.value = state.value.copy(phone = v)
    }

    fun updatePassword(v: String) {
        state.value = state.value.copy(password = v)
    }

    fun updateConfirm(v: String) {
        state.value = state.value.copy(confirm = v)
    }

    private fun validate(): String? {
        val s = state.value
        if (s.fullName.isBlank()) return "Ingresa tu nombre completo."
        if (s.phone.isBlank()) return "Ingresa tu número de teléfono."
        if (!s.email.contains("@")) return "Correo inválido."
        if (s.password.length < 8) return "La contraseña debe tener al menos 8 caracteres."
        if (s.password != s.confirm) return "Las contraseñas no coinciden."
        return null
    }

    fun register(onSuccess: () -> Unit) {
        val localError = validate()
        if (localError != null) {
            state.value = state.value.copy(error = localError)
            return
        }

        viewModelScope.launch {
            state.value = state.value.copy(isLoading = true, error = null)

            val res = repo.register(
                fullName = state.value.fullName.trim(),
                email = state.value.email.trim(),
                phone = state.value.phone.trim(),
                password = state.value.password,
                autoLogin = false  // login automático tras registrar
            )

            res.onSuccess {
                state.value = state.value.copy(isLoading = false, success = true)
                onSuccess()
            }.onFailure { e ->
                state.value = state.value.copy(
                    isLoading = false,
                    error = e.message ?: "No se pudo registrar. Intenta más tarde."
                )
            }
        }
    }
}
