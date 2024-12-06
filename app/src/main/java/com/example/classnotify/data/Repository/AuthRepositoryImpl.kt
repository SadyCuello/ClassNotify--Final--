package com.example.classnotify.data.Repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(private val firebaseAuth: FirebaseAuth) : AuthRepository {

    override suspend fun loginWithEmail(email: String, password: String): Boolean {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            authResult.user != null  // Retorna true si el usuario está autenticado
        } catch (e: Exception) {
            false  // Si ocurre un error, devuelve false
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String): Boolean {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            authResult.user != null
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Boolean {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            authResult.user != null
        } catch (e: Exception) {
            false
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
