package com.example.classnotify.ui_presentation.ui.view

import AgregarMateriaView
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.classnotify.domain.models.Routes
import com.example.classnotify.domain.models.UserRole
import com.example.classnotify.domain.viewModels.AnuncioViewModel
import com.example.classnotify.domain.viewModels.MateriaViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.example.classnotify.ui_presentation.ui.view.DrawerContent // Ajusta la ruta al archivo si es necesario



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavManager(
    navController: NavHostController,
    materiaViewModel: MateriaViewModel,
    anuncioViewModel: AnuncioViewModel,
    userRole: UserRole,
    idMateria: Long? = null
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

   // val backgroundModifier = if (drawerState.isOpen) {
       // Modifier.background(Color.White.copy(alpha = 0.1f))
  //  } else {
       // Modifier
    //}

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            DrawerContent(
                onNavigate = { route ->
                    coroutineScope.launch { drawerState.close() }
                    navController.navigate(route) {
                        popUpTo("inicio") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                isDrawerOpen = drawerState.isOpen,
                userRole = userRole
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (drawerState.isOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                      //  .background(Color.Black.copy(alpha = 0.90f)) // Fondo más oscuro cuando el Drawer está abierto
                        .clickable { coroutineScope.launch { drawerState.close() } }
                )
            }
            NavHost(navController = navController, startDestination = "inicio") {
                composable(Routes.INICIO) {
                    InicioView(
                        navController = navController,
                        materiaViewModel = materiaViewModel,
                        anuncioViewModel = anuncioViewModel,
                        userRole = userRole
                    )
                }
                composable(Routes.AGREGAR_MATERIA) {
                    AgregarMateriaView(
                        viewModel = materiaViewModel,
                        navController = navController
                    )
                }
                composable(Routes.VER_ANUNCIOS) {
                    VerAnunciosView(
                        navController = navController,
                        viewModel = anuncioViewModel
                    )
                }
                composable(Routes.PUBLICAR_ANUNCIO) {
                    PublicarAnuncioView(
                        navController = navController,
                        viewModel = anuncioViewModel
                    )
                }
                composable(Routes.EDITAR_MATERIAS) { backStackEntry ->
                    val idMateria = backStackEntry.arguments?.getLong("idMateria")
                    val materia = idMateria?.let {
                        runBlocking { materiaViewModel.obtenerMaterias(it) }
                    }
                    if (materia != null) {
                        EditarMateriaView(
                            navController = navController,
                            viewModel = materiaViewModel,
                            idMateria = idMateria
                        )
                    } else {
                        Text("Materia no encontrada.")
                    }
                }
            }
        }
    }
    @Composable
    fun DrawerContent(
        onNavigate: (String) -> Unit,
        isDrawerOpen: Boolean,
        userRole: UserRole
    ) {
        Column {
            Text(
                "Menu",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )

            // Aquí ajustamos el contenido según el rol del usuario
            when (userRole) {
                UserRole.ESTUDIANTE -> {
                    Button(onClick = { onNavigate("verAnuncios") }) {
                        Text("Ver Anuncios")
                    }
                }

                UserRole.ADMINISTRADOR -> {
                    Button(onClick = { onNavigate("agregarMateria") }) {
                        Text("Registrar Materia")
                    }
                    Button(onClick = { onNavigate("publicarAnuncio") }) {
                        Text("Publicar Anuncio")
                    }
                    Button(onClick = { onNavigate("verAnuncios") }) {
                        Text("Ver Anuncios y Materias")
                    }
                }
            }
        }
    }
}


