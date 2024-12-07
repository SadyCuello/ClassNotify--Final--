package com.example.classnotify

import LoginView
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import com.example.classnotify.data.Repository.AuthRepositoryImpl
import com.example.classnotify.domain.models.Routes
import com.example.classnotify.domain.usecase.LoginUseCase
import com.example.classnotify.domain.viewModels.LoginState
import com.example.classnotify.domain.viewModels.LoginViewModel
import com.example.classnotify.domain.viewModels.LoginViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest

class LoginActivity : AppCompatActivity() {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val authRepository by lazy { AuthRepositoryImpl(firebaseAuth) }
    private val loginUseCase by lazy { LoginUseCase(authRepository) }
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(loginUseCase)
    }
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginView(loginViewModel, ::onGoogleSignInClick)
        }

        configureGoogleSignIn()
        observeLoginState()
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        observeLoginState()
    }

    private fun observeLoginState() {
        lifecycleScope.launchWhenStarted {
            loginViewModel.loginState.collectLatest { state ->
                when (state) {
                    is LoginState.Loading -> {
                        Toast.makeText(this@LoginActivity, "Cargando...", Toast.LENGTH_SHORT).show()
                    }

                    is LoginState.Success -> {
                        Toast.makeText(
                            this@LoginActivity,
                            "Bienvenido, ${state.user.displayName}",
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateTo(Routes.INICIO)
                    }

                    is LoginState.Error -> {
                        Toast.makeText(
                            this@LoginActivity,
                            "Error: ${state.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    LoginState.Idle -> Unit
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
                    Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Sign-in canceled.", Toast.LENGTH_SHORT).show()
            }
        }

    fun onGoogleSignInClick() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun navigateTo(destination: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("destination", destination)
        }
        startActivity(intent)
        finish()
    }
}