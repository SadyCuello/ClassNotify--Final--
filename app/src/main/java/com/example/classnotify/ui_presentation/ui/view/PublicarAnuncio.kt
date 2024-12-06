package com.example.classnotify.ui_presentation.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.classnotify.domain.models.Anuncio
import com.example.classnotify.domain.viewModels.AnuncioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicarAnuncioView(
    navController: NavController,
    viewModel: AnuncioViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Publicar Anuncio",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        ContentPublicarAnuncioView(
            paddingValues = paddingValues,
            navController = navController,
            viewModel = viewModel,
            snackbarHostState = snackbarHostState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentPublicarAnuncioView(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: AnuncioViewModel,
    snackbarHostState: SnackbarHostState
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(top = 30.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text(text = "Título del anuncio") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
        )
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text(text = "Descripción del anuncio") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (titulo.isNotEmpty() && descripcion.isNotEmpty()) {
                    val anuncio = Anuncio(
                        titulo = titulo,
                        descripcion = descripcion
                    )
                    viewModel.agregarAnuncio(anuncio, onSuccess = {
                        navController.popBackStack()
                    }, onFailure = {
                        snackbarMessage = "Error al publicar el anuncio."
                    })
                    navController.popBackStack()
                } else {
                    snackbarMessage = "Por favor, complete todos los campos."
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp)
        ) {
            Text("Publicar")
        }
    }
}
