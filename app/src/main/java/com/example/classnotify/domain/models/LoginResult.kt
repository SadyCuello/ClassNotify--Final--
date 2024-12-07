package com.example.classnotify.domain.models

import com.google.firebase.auth.FirebaseUser

data class LoginResult(
    val user: FirebaseUser?,
    val role: String?
) {
    companion object {
        fun Success(currentUser: FirebaseUser, userRole: String): LoginResult {
            return LoginResult(user = currentUser, role = userRole)
        }
    }
}
