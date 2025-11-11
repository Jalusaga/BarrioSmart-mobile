package com.example.barriosmartfront.data.auth

object SessionManager {
    private var token: String? = null
    private var userId: Long? = null
    private var userName: String? = null
    private var userEmail: String? = null

    fun saveSession(email: String, token: String, userId:Long, userName:String) {
        this.userEmail = email
        this.token = token
        this.userId = userId
        this.userName = userName
    }

    fun getToken(): String? = token
    fun getUserEmail(): String? = userEmail
    fun getUserName(): String? = userName
    fun clearSession() {
        token = null
        userEmail = null
    }

    fun userId():Long? = userId
}
