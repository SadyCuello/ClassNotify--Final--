package com.example.classnotify.domain.models

import com.google.firebase.auth.FirebaseUser

data class LoginResult(
    val user: FirebaseUser?,
    val role: String?
)
