package com.example.classnotify.ui_presentation.ui.view

import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.classnotify.domain.models.UserRole

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
