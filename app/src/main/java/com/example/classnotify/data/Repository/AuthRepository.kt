package com.example.classnotify.data.Repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {

    // Cambiar para devolver Boolean
    suspend fun loginWithEmail(email: String, password: String): Boolean
    suspend fun signUpWithEmail(email: String, password: String): Boolean
    suspend fun loginWithGoogle(idToken: String): Boolean
    fun getCurrentUser(): FirebaseUser?
    suspend fun logout(): Result<Unit>
}
