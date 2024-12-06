package com.example.classnotify.domain.usecase

import com.example.classnotify.data.Repository.AuthRepository
import com.example.classnotify.domain.models.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LoginUseCase(val authRepository: AuthRepository) {

    // Función para hacer login con Google
    suspend fun loginWithGoogle(idToken: String): Boolean {
        // Llamamos al repositorio para autenticar con Google
        val authResult = authRepository.loginWithGoogle(idToken)
        return authResult != null // Si el resultado no es null, el login fue exitoso
    }

    // Función para logout
    suspend fun logout() {
        authRepository.logout()
    }

    // Función para login con email y contraseña
    suspend fun loginAndCheckRole(email: String, password: String): UserRole? {

        val loginSuccess = authRepository.loginWithEmail(email, password)
        return if (loginSuccess) {
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

            // Realiza la verificación asincrónica del rol del usuario
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
        return UserRole.ESTUDIANTE // Si no se encuentra el usuario o el rol, se retorna un rol por defecto
    }
}

