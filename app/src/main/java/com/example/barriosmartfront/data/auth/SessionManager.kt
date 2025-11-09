package com.example.barriosmartfront.data.auth

object SessionManager {
    private var token: String? = null
    private var userEmail: String? = null

    fun saveSession(email: String, token: String) {
        this.userEmail = email
        this.token = token
    }

    fun getToken(): String? = token
    fun getUserEmail(): String? = userEmail
    fun clearSession() {
        token = null
        userEmail = null
    }
}
