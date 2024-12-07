package com.example.classnotify.data.Repository

import com.example.classnotify.domain.models.LoginResult
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {

    suspend fun loginWithGoogle(idToken: String): LoginResult
    fun getCurrentUser(): FirebaseUser?
    suspend fun logout(): Result<Unit>
}

