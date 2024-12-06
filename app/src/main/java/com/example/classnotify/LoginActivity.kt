package com.example.classnotify

import LoginView
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.classnotify.domain.viewModels.LoginState
import com.example.classnotify.domain.viewModels.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.flow.collectLatest

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            LoginView(loginViewModel, ::onGoogleSignInClick)
        }

        // Configuración de GoogleSignInClient
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Usa tu client ID de Firebase
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        observeLoginState() // Observa los cambios en el estado de login
    }

    private fun observeLoginState() {
        lifecycleScope.launchWhenStarted {
            loginViewModel.loginState.collectLatest { state ->
                when (state) {
                    is LoginState.Loading -> {
                        // Mostrar un CircularProgressIndicator si está cargando
                        Toast.makeText(this@LoginActivity, "Cargando...", Toast.LENGTH_SHORT).show()
                    }

                    is LoginState.Success -> {
                        // Mostrar mensaje de bienvenida
                        Toast.makeText(
                            this@LoginActivity,
                            "Bienvenido, ${state.user.displayName}",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Redirigir según el rol del usuario
                        if (state.userRole == "admin") {
                            // Si es administrador, navega a la vista de administrador
                            val intent = Intent(this@LoginActivity, AdminMainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // Si es estudiante, navega a la vista de estudiante
                            val intent = Intent(this@LoginActivity, StudentMainActivity::class.java)
                            startActivity(intent)
                        }
                        finish() // Finaliza la actividad de Login para que el usuario no regrese
                    }

                    is LoginState.Error -> {
                        // Mostrar mensaje de error si ocurre un fallo
                        Toast.makeText(
                            this@LoginActivity,
                            "Error: ${state.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is LoginState.Idle -> {
                        // Estado inicial, sin acciones
                    }
                }
            }
        }
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    loginViewModel.loginWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    // Función que se llamará desde LoginView
   internal fun onGoogleSignInClick() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }
}
