package com.example.classnotify

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.classnotify.data.local.AnuncioDataBase
import com.example.classnotify.data.local.ClassDataBase
import com.example.classnotify.data.Remote.Api.AnuncioRepository
import com.example.classnotify.data.Repository.FirestoreRepository
import com.example.classnotify.domain.models.UserRole
import com.example.classnotify.domain.viewModels.AnuncioViewModel
import com.example.classnotify.domain.viewModels.MateriaViewModel
import com.example.classnotify.ui_presentation.ui.theme.ClassNotifyTheme
import com.example.classnotify.ui_presentation.ui.view.NavManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private companion object {
        const val RC_REQUEST_CODE = 100
    }

    private lateinit var anuncioViewModel: AnuncioViewModel
    private lateinit var materiaViewModel: MateriaViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isAuthenticated = checkIfUserIsAuthenticated()

        if (!isAuthenticated) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            FirebaseApp.initializeApp(this)
            val firestore = FirebaseFirestore.getInstance()
            requestStoragePermissions()

            val classDataBase = Room.databaseBuilder(
                applicationContext,
                ClassDataBase::class.java,
                "class_database"
            ).build()

            val materiaDao = classDataBase.materiaDao()
            val anuncioDataBase = Room.databaseBuilder(
                applicationContext,
                AnuncioDataBase::class.java,
                "anuncio_database"
            ).build()

            val anuncioDao = anuncioDataBase.anuncioDao()
            val anuncioRepository = AnuncioRepository(anuncioDao)
            val firestoreRepository = FirestoreRepository(firestore)

            anuncioViewModel = AnuncioViewModel(application, anuncioRepository, firestoreRepository)
            materiaViewModel = MateriaViewModel(materiaDao, firestoreRepository)

            setContent {
                ClassNotifyTheme {
                    val navController = rememberNavController()
                    NavManager(
                        navController = navController,
                        materiaViewModel = materiaViewModel,
                        anuncioViewModel = anuncioViewModel,
                        userRole = UserRole.ESTUDIANTE
                    )
                }
            }
        }
    }
        private fun requestStoragePermissions() {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    RC_REQUEST_CODE
                )
            }
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == RC_REQUEST_CODE) {
                when {
                    grantResults.isEmpty() -> Log.e(
                        "MainActivity",
                        "No se proporcionaron resultados"
                    )

                    grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                        Toast.makeText(
                            this,
                            "Permiso de almacenamiento concedido",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        Toast.makeText(
                            this,
                            "Permiso de almacenamiento denegado. La aplicación puede no funcionar correctamente.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    private fun checkIfUserIsAuthenticated(): Boolean {
        // Verificar autenticación con Firebase
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            // Si el usuario está autenticado en Firebase, retornamos true
            return true
        }

        // Verificar estado de sesión de Google
        val googleAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (googleAccount != null) {
            // Si el usuario está autenticado en Google, retornamos true
            return true
        }

        // Si no está autenticado ni con Firebase ni con Google, retornamos false
        return false
    }
    }