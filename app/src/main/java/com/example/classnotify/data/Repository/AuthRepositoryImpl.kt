package com.example.classnotify.data.Repository

import com.example.classnotify.domain.models.LoginResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(private val firebaseAuth: FirebaseAuth) : AuthRepository {

    override suspend fun loginWithGoogle(idToken: String): LoginResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user
            if (user != null) {
                LoginResult(user = user, role = null) // Puedes asignar el rol después
            } else {
                throw Exception("Error: usuario no encontrado.")
            }
        } catch (e: Exception) {
            throw Exception("Error al iniciar sesión: ${e.message}")
        }
    }

    // Método para obtener el usuario actual (puedes usarlo para verificar si el usuario está logueado)
    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    // Método para cerrar sesión
    override suspend fun logout(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e) // Devuelve un error si la desconexión falla
        }
    }
}
