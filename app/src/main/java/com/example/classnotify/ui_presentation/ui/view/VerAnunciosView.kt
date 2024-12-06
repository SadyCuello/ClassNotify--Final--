package com.example.classnotify.ui_presentation.ui.view

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.classnotify.domain.models.Anuncio
import com.example.classnotify.domain.viewModels.AnuncioViewModel
import com.example.classnotify.ui_presentation.ui.states.AnuncioStates

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerAnunciosView(
    navController: NavController,
    viewModel: AnuncioViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val anuncioStates by viewModel.anuncioStates.observeAsState(AnuncioStates())

    val anuncios = anuncioStates.listaAnuncios
    Log.d("VerAnunciosView", "Anuncios en la vista: $anuncios")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Ver Anuncios",
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
        ContentVerAnunciosView(
            paddingValues = paddingValues,
            navController = navController,
            anuncios = anuncios
        )
    }
}

@Composable
fun ContentVerAnunciosView(
    paddingValues: PaddingValues,
    navController: NavController,
    anuncios: List<Anuncio>
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(top = 16.dp)
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            items(anuncios) { anuncio ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            text = anuncio.titulo,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = anuncio.descripcion,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
