package com.example.classnotify.domain.usecase

import com.example.classnotify.data.Repository.AuthRepository
import com.example.classnotify.domain.models.LoginResult
import com.example.classnotify.domain.models.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LoginUseCase(val authRepository: AuthRepository) {

    suspend fun loginWithGoogle(idToken: String): LoginResult {
        return authRepository.loginWithGoogle(idToken)
    }

    suspend fun logout() {
        authRepository.logout()
    }

    suspend fun loginAndCheckRole(idToken: String): UserRole? {
        val loginResult = authRepository.loginWithGoogle(idToken)
        return if (loginResult.user != null) {
            verificarRolUsuario()
        } else {
            null
        }
    }

    suspend fun verificarRolUsuario(): UserRole {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("usuarios").document(currentUser.uid)


            val document = userRef.get().await()
            if (document.exists()) {
                // Obtén el rol del documento de Firestore
                val role = document.getString("role")
                return when (role) {
                    "administrador" -> UserRole.ADMINISTRADOR
                    else -> UserRole.ESTUDIANTE
                }
            }
        }
        return UserRole.ESTUDIANTE
    }
}

