package com.example.classnotify.domain.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classnotify.domain.models.LoginResult
import com.example.classnotify.domain.usecase.LoginUseCase
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Estados de la autenticación
sealed class LoginState {
    object Loading : LoginState()
    data class Success(val user: FirebaseUser, val userRole: String) : LoginState()
    data class Error(val message: String) : LoginState()
    object Idle : LoginState() // Estado inicial
}

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> get() = _loginState

    // Método para login con Google
    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading
                val result = loginUseCase.loginWithGoogle(idToken) as LoginResult

                // Verificar si result contiene un usuario
                val user = result.user // `result` es de tipo LoginResult
                if (user != null) {
                    val userRole = getUserRoleFromDatabase(user)
                    _loginState.value = LoginState.Success(user, userRole)
                } else {
                    _loginState.value = LoginState.Error("Usuario no encontrado")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Error al iniciar sesión con Google")
            }
        }
    }

    // Método para obtener el rol del usuario desde la base de datos
    private fun getUserRoleFromDatabase(user: FirebaseUser): String {
        // Lógica para obtener el rol desde Firebase, Firestore, o la base de datos
        return if (user.email?.contains("admin") == true) {
            "admin"
        } else {
            "student"
        }
    }

    // Método de logout
    fun logout() {
        viewModelScope.launch {
            try {
                loginUseCase.logout()
                _loginState.value = LoginState.Idle // Cambia el estado a Idle al cerrar sesión
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Error al cerrar sesión")
            }
        }
    }
}

