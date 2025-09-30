// data/AuthRepository.kt
package com.example.barriosmartfront.data

import com.example.barriosmartfront.data.auth.AuthService
import com.example.barriosmartfront.data.auth.ITokenStore
import com.example.barriosmartfront.data.auth.LoginRequest
import com.example.barriosmartfront.data.auth.UserResponse
import com.example.barriosmartfront.data.auth.UsersService
import com.example.barriosmartfront.data.network.dto.RegisterRequest


class AuthRepository(
    private val authService: AuthService,
    private val usersService: UsersService,
    private val tokenStore: ITokenStore
) {

    // Login: guarda token en DataStore
    suspend fun login(email: String, password: String): Result<Unit> = safeApi {
        val res = authService.login(LoginRequest(email, password))
        tokenStore.save(res.access_token)
    }

    // Register: crea usuario. Opcional: auto-login después de registrar.
    suspend fun register(
        fullName: String,
        email: String,
        phone: String?,
        password: String,
        autoLogin: Boolean = true
    ): Result<UserResponse> = safeApi {
        val created = usersService.register(
            RegisterRequest(
                full_name = fullName,
                email = email,
                phone = phone,
                password = password,
                is_admin = false,
                is_active = true
            )
        )
        if (autoLogin) {
            // intenta login con las mismas credenciales
            runCatching { login(email, password).getOrThrow() }
        }
        created
    }

    // Logout simple: borra el token
    suspend fun logout(): Result<Unit> = safeApi {
        tokenStore.clear()
    }

    // Helper para mapear errores a Result
    private inline fun <T> safeApi(block: () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: retrofit2.HttpException) {
            val msg = when (e.code()) {
                400, 401 -> "Credenciales o datos inválidos."
                409      -> "Conflicto (posible email ya registrado)."
                422      -> "Datos inválidos (422)."
                in 500..599 -> "Error del servidor."
                else -> "HTTP ${e.code()}."
            }
            Result.failure(RuntimeException(msg, e))
        } catch (e: java.io.IOException) {
            Result.failure(RuntimeException("Sin conexión. Intenta de nuevo.", e))
        } catch (e: Exception) {
            Result.failure(RuntimeException("Ocurrió un error.", e))
        }
    }
}

