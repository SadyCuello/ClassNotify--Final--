package com.example.classnotify.ui_presentation.ui.view

import AgregarMateriaView
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.classnotify.domain.models.UserRole
import com.example.classnotify.domain.viewModels.AnuncioViewModel
import com.example.classnotify.domain.viewModels.MateriaViewModel
import com.example.classnotify.R
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavManager(
    navController: NavHostController,
    materiaViewModel: MateriaViewModel,
    anuncioViewModel: AnuncioViewModel,
    userRole: UserRole // Usamos el enum UserRole aquí
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val backgroundModifier = if (drawerState.isOpen) {
        Modifier.background(Color.Black.copy(alpha = 0.30f)) // Fondo difuminado
    } else {
        Modifier
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            DrawerContent(
                onNavigate = { route ->
                    coroutineScope.launch {
                        drawerState.close()
                    }
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                isDrawerOpen = drawerState.isOpen,
                userRole = userRole // Pasamos el rol del usuario
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (drawerState.isOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.90f)) // Fondo más oscuro cuando el Drawer está abierto
                        .clickable { coroutineScope.launch { drawerState.close() } }
                )
            }
            NavHost(navController = navController, startDestination = "inicio") {
                composable("inicio") {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable {
                                            coroutineScope.launch {
                                                drawerState.open()
                                            }
                                        }
                                    ) {
                                        val logo: Painter = painterResource(id = R.drawable.images)
                                        Icon(painter = logo, contentDescription = "Logo", modifier = Modifier.size(40.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Class Notify", style = MaterialTheme.typography.titleLarge)
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color.Red
                                )
                            )
                        },
                        floatingActionButton = {
                            if (userRole == UserRole.ADMINISTRADOR) { // Solo administradores pueden agregar materias
                                FloatingActionButton(
                                    onClick = { navController.navigate("agregarMateria") },
                                    content = { Icon(Icons.Default.Menu, contentDescription = "Agregar Materia") }
                                )
                            }
                        },
                        content = { paddingValues ->
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("Bienvenido a la pantalla de inicio", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    )
                }
                composable("agregarMateria") {
                    if (userRole == UserRole.ADMINISTRADOR) { // Solo administradores pueden registrar materias
                        AgregarMateriaView(viewModel = materiaViewModel, navController = navController)
                    } else {
                        // Mostrar un mensaje de error si no es administrador
                        Text("Solo administradores pueden agregar materias", color = Color.Red)
                    }
                }
                composable("verAnuncios") {
                    VerAnunciosView(navController = navController, viewModel = anuncioViewModel)
                }
                composable("publicarAnuncio") {
                    if (userRole == UserRole.ADMINISTRADOR) { // Solo administradores pueden publicar anuncios
                        PublicarAnuncioView(navController = navController, viewModel = anuncioViewModel)
                    } else {
                        // Mensaje para los estudiantes si intentan acceder
                        Text("Solo los administradores pueden publicar anuncios", color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerItem(label: String, route: String, onNavigate: (String) -> Unit) {
    Text(
        text = label,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigate(route) }
            .padding(16.dp)
    )
}

@Composable
fun DrawerContent(onNavigate: (String) -> Unit, isDrawerOpen: Boolean, userRole: UserRole) {
    if (isDrawerOpen) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "",
                style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Divider(color = Color.Gray)

            // Mostrar las opciones del Drawer según el rol del usuario
            DrawerItem(label = "Inicio", route = "inicio", onNavigate = onNavigate)
            DrawerItem(label = "Ver Anuncios", route = "verAnuncios", onNavigate = onNavigate)

            // Si el usuario es administrador, agregar las opciones de registrar materias y publicar anuncios
            if (userRole == UserRole.ADMINISTRADOR) {
                DrawerItem(label = "Publicar Anuncio", route = "publicarAnuncio", onNavigate = onNavigate)
                DrawerItem(label = "Agregar Materia", route = "agregarMateria", onNavigate = onNavigate)
            } else {
                // Estudiantes no ven las opciones de administrar materias o publicar anuncios
                DrawerItem(label = "Ver Anuncios", route = "verAnuncios", onNavigate = onNavigate)
            }
        }
    }
}
